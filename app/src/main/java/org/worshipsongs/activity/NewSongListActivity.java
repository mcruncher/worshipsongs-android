package org.worshipsongs.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/17/2015.
 */
public class NewSongListActivity extends FragmentActivity {

    private ListView songListView;
    private List<String> songNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    private FragmentManager fragmentManager;
    private CommonService commonService = new CommonService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        songNames = intent.getStringArrayListExtra("songNames");
        songListView = (ListView) findViewById(R.id.song_list_view);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        adapter = adapterService.getSongListAdapter(songNames, fragmentManager);
        adapterService.setServiceNames(commonService.readServiceName());
        songListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = adapterService.getSongListAdapter(getFilteredSong(newText), fragmentManager);
                songListView.setAdapter(adapterService.getSongListAdapter(getFilteredSong(newText), fragmentManager));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getSongListAdapter(getFilteredSong(query), fragmentManager);
                songListView.setAdapter(adapterService.getSongListAdapter(getFilteredSong(query), fragmentManager));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private List<String> getFilteredSong(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String song : songNames) {
            if (song.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
