package org.worshipsongs.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;


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
import org.worshipsongs.domain.Verse;
import org.worshipsongs.fragment.AddPlayListsDialogFragment;
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
    private String selectedSong;
    private List<Verse> verseList;
    private String[] serviceNames;
    //private ListDialogFragment dialogFragment;
    private WorshipSongApplication application = new WorshipSongApplication();
    private CustomTagColorService customTagColorService = new CustomTagColorService();
    private SongDao songDao = new SongDao(application.getContext());
    private UtilitiesService utilitiesService = new UtilitiesService();
    private CommonService commonService = new CommonService();

    public ArrayAdapter<Song> getNewSongListAdapter(final List<Song> songs, final FragmentManager fragmentManager)
    {
        return new ArrayAdapter<Song>(application.getContext(), R.layout.songs_listview_content, songs)
        {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent)
            {
                LayoutInflater inflater = (LayoutInflater) application.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.songs_listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.songsTextView);
                textView.setText(songs.get(position).getTitle());
                final ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);

                imageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Song song = songDao.findContentsByTitle(String.valueOf(textView.getText()));
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.valueOf(textView.getText())).append("\n").append("\n");
                        for (String content : song.getContents()) {
                            for (String formattedContent : customTagColorService.getFormattedLines(content)) {
                                builder.append(formattedContent);
                                builder.append("\n");
                            }
                            builder.append("\n");
                        }
                        builder.append(application.getContext().getString(R.string.share_info));
                        showSharePopupmenu(view, String.valueOf(textView.getText()), fragmentManager, builder.toString());
                    }
                });

                (rowView.findViewById(R.id.songsTextView)).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        selectedSong = textView.getText().toString();
                        displaySelectedSong(songs, position);
                    }
                });
                return rowView;
            }
        };
    }

    private void showYouTube()
    {
        Intent lightboxIntent = new Intent(application.getContext(), CustomYoutubeBoxActivity.class);
        lightboxIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, "yKc-ey5pnNo");
        lightboxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.getContext().startActivity(lightboxIntent);
    }

    public void showSharePopupmenu(View view, final String songName, final FragmentManager fragmentManager, final String content)
    {
        Context wrapper = new ContextThemeWrapper(application.getContext(), R.style.PopupMenu_Theme);
        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(wrapper, view, Gravity.RIGHT);
        } else {
            popupMenu = new PopupMenu(wrapper, view);
        }
        popupMenu.getMenuInflater().inflate(R.menu.favourite_share_option_menu, popupMenu.getMenu());
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
                        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
                        textShareIntent.putExtra(Intent.EXTRA_TEXT, content);
                        textShareIntent.setType("text/plain");
                        Intent intent = Intent.createChooser(textShareIntent, "Share " + songName + " with...");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        application.getContext().startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }



    public void displaySelectedSong(List<Song> songs, int position)
    {
        Intent intent = new Intent(application.getContext(), SongContentViewActivity.class);
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
        application.getContext().startActivity(intent);

    }

}
