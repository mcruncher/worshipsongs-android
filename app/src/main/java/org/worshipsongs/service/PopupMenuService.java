package org.worshipsongs.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.FavouritesDialogFragment;
import org.worshipsongs.domain.Song;
import org.worshipsongs.utils.PermissionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.Context.PRINT_SERVICE;
import static org.worshipsongs.WorshipSongApplication.getContext;

/**
 * author: Madasamy,Seenivasan, Vignesh Palanisamy
 * version: 1.0.0
 */

public class PopupMenuService
{

    private CustomTagColorService customTagColorService = new CustomTagColorService();
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private SongDao songDao = new SongDao(getContext());

    public void showPopupmenu(final AppCompatActivity activity, final View view, final String songName, boolean hidePlay)
    {
        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenu_Theme);
        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(wrapper, view, Gravity.RIGHT);
        } else {
            popupMenu = new PopupMenu(wrapper, view);
        }
        popupMenu.getMenuInflater().inflate(R.menu.favourite_share_option_menu, popupMenu.getMenu());
        final Song song = songDao.findContentsByTitle(songName);
        final String urlKey = song.getUrlKey();
        MenuItem menuItem = popupMenu.getMenu().findItem(R.id.play_song);
        menuItem.setVisible(urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo() && hidePlay);
        MenuItem exportMenuItem = popupMenu.getMenu().findItem(R.id.export_pdf);
        exportMenuItem.setVisible(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT);
        MenuItem presentSongMenuItem = popupMenu.getMenu().findItem(R.id.present_song);
        presentSongMenuItem.setVisible(false);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(final MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.addToList:
                        FavouritesDialogFragment favouritesDialogFragment = FavouritesDialogFragment.newInstance(songName);
                        favouritesDialogFragment.show(activity.getSupportFragmentManager(), "FavouritesDialogFragment");
                        return true;
                    case R.id.share_whatsapp:
                        shareSongInSocialMedia(songName, song);
                        return true;
                    case R.id.play_song:
                        showYouTube(urlKey, songName);
                        return true;
                    case R.id.present_song:
                        startPresentActivity(songName);
                        return true;
                    case R.id.export_pdf:
                        if (PermissionUtils.isStoragePermissionGranted(activity)) {
                            exportSongToPDF(songName, song);
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void shareSongInSocialMedia(String songName, Song song)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(songName).append("\n").append("\n");
        for (String content : song.getContents()) {
            builder.append(customTagColorService.getFormattedLines(content));
            builder.append("\n");
        }
        builder.append(getContext().getString(R.string.share_info));
        Log.i(PopupMenuService.this.getClass().getSimpleName(), builder.toString());
        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
        textShareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        textShareIntent.setType("text/plain");
        Intent intent = Intent.createChooser(textShareIntent, "Share " + songName + " with...");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void exportSongToPDF(String songName, Song song)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), songName + ".pdf");

        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 450, 700)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();
        PdfDocument document = new PrintedPdfDocument(WorshipSongApplication.getContext(), printAttrs);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(450, 700, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        if (page != null) {
            Paint titleDesign = new Paint();
            titleDesign.setTextAlign(Paint.Align.LEFT);
            titleDesign.setTextSize(18);
            String title = song.getTamilTitle() + "/" + songName;
            float titleLength = titleDesign.measureText(title);
            float yPos = 50;
            if (page.getCanvas().getWidth() > titleLength) {
                int xPos = (page.getCanvas().getWidth() / 2) - (int) titleLength / 2;
                page.getCanvas().drawText(title, xPos, 20, titleDesign);
            } else {
                int xPos = (page.getCanvas().getWidth() / 2) - (int) titleDesign.measureText(song.getTamilTitle()) / 2;
                page.getCanvas().drawText(song.getTamilTitle() + "/", xPos, 20, titleDesign);
                xPos = (page.getCanvas().getWidth() / 2) - (int) titleDesign.measureText(songName) / 2;
                page.getCanvas().drawText(songName, xPos, 45, titleDesign);
                yPos = 75;
            }
            for (String content : song.getContents()) {
                if (yPos > 620) {
                    document.finishPage(page);
                    page = document.startPage(pageInfo);
                    yPos = 40;
                }
                yPos = customTagColorService.getFormattedPage(content, page, 10, yPos);
                yPos = yPos + 20;
            }
        }
        document.finishPage(page);
        try {
            OutputStream os = new FileOutputStream(file);
            document.writeTo(os);
            document.close();
            os.close();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

            Intent intent = Intent.createChooser(shareIntent, "Share " + songName + " with...");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            Log.i("done", file.getAbsolutePath().toString());

        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }

    private void showYouTube(String urlKey, String songName)
    {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(getContext(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        youTubeIntent.putExtra("title", songName);
        youTubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(youTubeIntent);
    }

    private void startPresentActivity(String title)
    {
        Intent intent = new Intent(getContext(), PresentSongActivity.class);
        intent.putExtra(CommonConstants.TITLE_KEY, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
