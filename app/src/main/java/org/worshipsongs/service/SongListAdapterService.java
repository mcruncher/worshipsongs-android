package org.worshipsongs.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.ListDialogFragment;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.PRINT_SERVICE;
import static org.worshipsongs.WorshipSongApplication.getContext;

/**
 * author: Madasamy,Seenivasan, Vignesh Palanisamy
 * version: 1.0.0
 */
@Deprecated
public class SongListAdapterService
{

    private CustomTagColorService customTagColorService = new CustomTagColorService();
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    private SongDao songDao = new SongDao(getContext());

    public ArrayAdapter<Song> getSongListAdapter(final List<Song> songs, final FragmentManager fragmentManager)
    {
        return new ArrayAdapter<Song>(getContext(), R.layout.songs_listview_content, songs)
        {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent)
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.songs_listview_content, parent, false);
                String title = songs.get(position).getTitle();
                setRelativeLayout(rowView, songs, position);
                setTitleTextView(rowView, songs, position);
                setPlayImageView(rowView, songs.get(position), fragmentManager);
                setImageView(rowView, title, fragmentManager);
                return rowView;
            }
        };
    }

    private void setRelativeLayout(View rowView, final List<Song> songs, final int position)
    {
        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.songs_list_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displaySelectedSong(songs, position);
            }
        });
    }

    private void setTitleTextView(View rowView, List<Song> songs, int position)
    {
        TextView textView = (TextView) rowView.findViewById(R.id.title);
        textView.setText((preferenceSettingService.isTamil() && songs.get(position).getTamilTitle().length() > 0) ?
                songs.get(position).getTamilTitle() : songs.get(position).getTitle());
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(songs.get(position).getTitle())) {
            textView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        }
    }

    private void displaySelectedSong(List<Song> songs, int position)
    {
        Setting.getInstance().setPosition(0);
        ArrayList<String> titleList = new ArrayList<String>();
        titleList.add(songs.get(position).getTitle());
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titleList);

        Intent intent = new Intent(getContext(), SongContentViewActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    private void setPlayImageView(final View rowView, final Song song, FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.play_imageview);
        final String urlKey = song.getUrlKey();
        if (urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo()) {
            imageView.setVisibility(View.VISIBLE);
        }
        imageView.setOnClickListener(onClickPopupListener(song.getTitle(), fragmentManager));
    }

    private void setImageView(View rowView, final String songTitle, final FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);
        imageView.setOnClickListener(onClickPopupListener(songTitle, fragmentManager));
    }

    private View.OnClickListener onClickPopupListener(final String title, final FragmentManager fragmentManager)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPopupmenu(view, title, fragmentManager, true);
            }
        };
    }

    public void showPopupmenu(final View view, final String songName, final FragmentManager fragmentManager, boolean hidePlay)
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
        MenuItem presentSongMenuItem = popupMenu.getMenu().findItem(R.id.present_song);
        presentSongMenuItem.setVisible(false);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(final MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.addToList:
                        ListDialogFragment listDialogFragment = ListDialogFragment.newInstance(songName);
                        listDialogFragment.show(fragmentManager, "ListDialogFragment");
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
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            exportSongToPDF(songName, song);
                        } else {
                            shareSongInSocialMedia(songName, song);
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
        Log.i(SongListAdapterService.this.getClass().getSimpleName(), builder.toString());
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
            String title = song.getTamilTitle() + "/" + songName;
            float titleLength = titleDesign.measureText(title);
            float i = 50;
            if(page.getCanvas().getWidth() > titleLength) {
                int xPos = (page.getCanvas().getWidth() / 2) - (int) titleLength / 2;
                page.getCanvas().drawText(title, xPos, 20, titleDesign);
            } else {
                int xPos = (page.getCanvas().getWidth() / 2) - (int) titleDesign.measureText(song.getTamilTitle()) / 2;
                page.getCanvas().drawText(song.getTamilTitle() + "/", xPos, 20, titleDesign);
                xPos = (page.getCanvas().getWidth() / 2) - (int) titleDesign.measureText(songName) / 2;
                page.getCanvas().drawText(songName, xPos, 35, titleDesign);
                i = 65;
            }
            for (String content : song.getContents()) {
                if(i > 620) {
                    document.finishPage(page);
                    page = document.startPage(pageInfo);
                    i = 40;
                }
                i = customTagColorService.getFormattedPage(content, page, 10, i);
                i = i + 20;
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
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));

            Intent intent = Intent.createChooser(shareIntent, "Share " + songName + " with...");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);

            Toast.makeText(getContext(), "PDF exported, check in download folder", Toast.LENGTH_LONG);
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
