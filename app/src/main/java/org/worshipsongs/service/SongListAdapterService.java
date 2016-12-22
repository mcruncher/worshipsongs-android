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
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.fragment.ListDialogFragment;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Madasamy,Seenivasan
 * version: 1.0.0
 */
public class SongListAdapterService
{

    private CustomTagColorService customTagColorService = new CustomTagColorService();
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());

    public ArrayAdapter<Song> getNewSongListAdapter(final List<Song> songs, final FragmentManager fragmentManager)
    {
        return new ArrayAdapter<Song>(WorshipSongApplication.getContext(), R.layout.songs_listview_content, songs)
        {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent)
            {
                LayoutInflater inflater = (LayoutInflater) WorshipSongApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.songs_listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.songsTextView);
                textView.setText(songs.get(position).getTitle());
                final ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);

                imageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        showPopupmenu(view, String.valueOf(textView.getText()), fragmentManager, true);
                    }
                });

                (rowView.findViewById(R.id.songsTextView)).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        displaySelectedSong(songs, position);
                    }
                });
                return rowView;
            }
        };
    }

    public void showPopupmenu(View view, final String songName, final FragmentManager fragmentManager, boolean hidePlay)
    {
        Context wrapper = new ContextThemeWrapper(WorshipSongApplication.getContext(), R.style.PopupMenu_Theme);
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
        menuItem.setVisible(urlKey != null && urlKey.length() > 0 &&preferenceSettingService.getPlayVideoStatus() && hidePlay);
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
                        StringBuilder builder = new StringBuilder();
                        builder.append(songName).append("\n").append("\n");
                        for (String content : song.getContents()) {
                            for (String formattedContent : customTagColorService.getFormattedLines(content)) {
                                builder.append(formattedContent);
                                builder.append("\n");
                            }
                            builder.append("\n");
                        }
                        builder.append(WorshipSongApplication.getContext().getString(R.string.share_info));
                        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
                        textShareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                        textShareIntent.setType("text/plain");
                        Intent intent = Intent.createChooser(textShareIntent, "Share " + songName + " with...");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        WorshipSongApplication.getContext().startActivity(intent);
                        return true;
                    case R.id.play_song:
                        showYouTube(urlKey, songName);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showYouTube(String urlKey, String songName)
    {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(WorshipSongApplication.getContext(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        youTubeIntent.putExtra("title", songName);
        youTubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WorshipSongApplication.getContext().startActivity(youTubeIntent);
    }

    private void displaySelectedSong(List<Song> songs, int position)
    {
        Intent intent = new Intent(WorshipSongApplication.getContext(), SongContentViewActivity.class);
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
        WorshipSongApplication.getContext().startActivity(intent);
    }
}
