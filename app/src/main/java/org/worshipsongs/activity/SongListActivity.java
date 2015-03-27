package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class SongListActivity extends Activity {

    private ListView songListView;
    private VerseParser verseparser;
    private String songTitle;
    private SongDao songDao;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<String> adapter;
    private String[] dataArray;
    private Song song;

    private Context context = WorshipSongApplication.getContext();
    private File serviceFile = null;
    List<String> songName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        songName = intent.getStringArrayListExtra("songNames");
        songListView = (ListView) findViewById(R.id.song_list_view);
        songDao = new SongDao(this);
        verseparser = new VerseParser();
        loadSongs();
    }

    private void loadSongs()
    {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songName);
        songListView.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
