package org.worshipsongs.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Seenivasan, Madasamy
 * version: 2.1.0
 */
public class SongListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private ListView songListView;
    private List<String> songNames = new ArrayList<String>();
    private ArrayAdapter<Song> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    private FragmentManager fragmentManager;
    private CommonService commonService = new CommonService();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        songNames = intent.getStringArrayListExtra("songNames");
        songListView = (ListView) findViewById(R.id.song_list_view);
        String title = intent.getStringExtra("title");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
        fragmentManager = getSupportFragmentManager();
        List<Song> songs = new ArrayList<>();
        for (String songTitle : songNames) {
            Song song = new Song();
            song.setTitle(songTitle);
            songs.add(song);
        }
        adapter = adapterService.getSongListAdapter(songs, fragmentManager);
        initializeServiceNames();
        songListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //TODO:Add search view
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.default_action_bar_menu, menu);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter = adapterService.getSongListAdapter(getFilteredSong(newText), fragmentManager);
//                songListView.setAdapter(adapterService.getSongListAdapter(getFilteredSong(newText), fragmentManager));
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter = adapterService.getSongListAdapter(getFilteredSong(query), fragmentManager);
//                songListView.setAdapter(adapterService.getSongListAdapter(getFilteredSong(query), fragmentManager));
//                return true;
//            }
//        };
//        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private List<String> getFilteredSong(String text)
    {
        List<String> filteredSongs = new ArrayList<String>();
        for (String song : songNames) {
            if (song.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onRefresh()
    {
        initializeServiceNames();
    }

    private void initializeServiceNames()
    {
        List<String> serviceNames = new ArrayList<String>();
        serviceNames.addAll(commonService.readServiceName());
        // adapterService.setServiceNames(serviceNames);
    }
}
