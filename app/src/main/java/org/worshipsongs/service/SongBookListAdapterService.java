package org.worshipsongs.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.dao.SongBookDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seenivasan on 5/17/2015.
 */
public class SongBookListAdapterService {

    List<String> songName = new ArrayList<String>();
    private WorshipSongApplication application = new WorshipSongApplication();
    private SongBookDao songBookDao = new SongBookDao(application.getContext());
    private List<Song> songs = new ArrayList<Song>();
    private SongDao songDao = new SongDao(application.getContext());

    public ArrayAdapter<String> getSongBookListAdapter(final List<String> songBookNames, final FragmentManager fragmentManager) {
        return new ArrayAdapter<String>(application.getContext(), R.layout.songs_listview_content, songBookNames) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) application.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.listTextView);
                textView.setText(songBookNames.get(position));

                (rowView.findViewById(R.id.listTextView)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String bookName = textView.getText().toString();
                        SongBook selectedBook = songBookDao.findBookByName(bookName);
                        songs = songDao.getSongTitlesByBookId(selectedBook.getId());
                        for (Song song : songs) {
                            songName.add(song.getTitle());
                        }
                        Collections.sort(songName);
                        Intent intent = new Intent(application.getContext(), SongListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putStringArrayListExtra("songNames", new ArrayList<String>(songName));
                        application.getContext().startActivity(intent);
                    }
                });
                return rowView;
            }
        };
    }
}
