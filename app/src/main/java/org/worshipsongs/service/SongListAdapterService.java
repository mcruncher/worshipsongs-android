package org.worshipsongs.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.dialog.ListDialogFragment;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

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
                setTextView(rowView, songs, position);
                setPlayImageView(rowView, songs.get(position), fragmentManager);
                setImageView(rowView, title, fragmentManager);
                return rowView;
            }
        };
    }

    private void setTextView(View rowView, final List<Song> songs, final int position)
    {
        String title = songs.get(position).getTitle();
        TextView textView = (TextView) rowView.findViewById(R.id.songsTextView);
        textView.setText(title);
        textView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                displaySelectedSong(songs, position);
            }
        });
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(songs.get(position).getTitle())) {
            textView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        }
    }

    private void displaySelectedSong(List<Song> songs, int position)
    {
        Intent intent = new Intent(getContext(), SongContentViewActivity.class);
        ArrayList<String> songList = new ArrayList<String>();
        for (Song song : songs) {
            songList.add(song.getTitle());
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, songList);
        Setting.getInstance().setPosition(position);
        //bundle.putInt(CommonConstants.POSITION_KEY, position);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    private void setPlayImageView(final View rowView, final Song song, FragmentManager fragmentManager) {
        ImageView imageView = (ImageView)rowView.findViewById(R.id.play_imageview);
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

    public void showPopupmenu(View view, final String songName, final FragmentManager fragmentManager, boolean hidePlay)
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
