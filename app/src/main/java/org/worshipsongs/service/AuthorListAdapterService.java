package org.worshipsongs.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seenivasan on 5/17/2015.
 */
public class AuthorListAdapterService {

    List<String> songNames;
    private WorshipSongApplication application = new WorshipSongApplication();
    private AuthorDao authorDao = new AuthorDao(application.getContext());
    private AuthorSongDao authorSongDao = new AuthorSongDao(application.getContext());
    private SongDao songDao = new SongDao(application.getContext());
    private List<AuthorSong> authorSongs = new ArrayList<AuthorSong>();
    private List<Song> songs;

    public ArrayAdapter<String> getAuthorListAdapter(final List<String> authors, final FragmentManager fragmentManager) {
        return new ArrayAdapter<String>(application.getContext(), R.layout.songs_listview_content, authors) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) application.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.listTextView);
                textView.setText(authors.get(position));

                (rowView.findViewById(R.id.listTextView)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String authorName = textView.getText().toString();
                        Author selectedAuthor = authorDao.findAuthorByName(authorName);
                        authorSongs = authorSongDao.findSongId(selectedAuthor.getId());
                        Log.d(this.getClass().getName(), "Author songs count" + authorSongs.size());
                        songs = new ArrayList<Song>();
                        for (AuthorSong authorSong : authorSongs) {
                            try {
                                if (authorSong.getSongId() > 0) {

                                    Song songById = songDao.getSongById(authorSong.getSongId());
                                    if (!songById.getTitle().isEmpty()) {
                                        Log.d(this.getClass().getName(), "Song ID Title" + songById.getTitle());
                                        songs.add(songById);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        songNames = new ArrayList<String>();
                        for (Song song : songs) {
                            songNames.add(song.getTitle());
                        }
                        Collections.sort(songNames);
                        Intent intent = new Intent(application.getContext(), SongListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putStringArrayListExtra("songNames", new ArrayList<String>(songNames));
                        application.getContext().startActivity(intent);
                    }
                });
                return rowView;
            }
        };
    }
}
