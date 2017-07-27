package org.worshipsongs.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.ServiceSongAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: Seenivasan, Madasamy
 * version 1.0.0
 */
public class ServiceSongsActivity extends AppCompatActivity
{
    private ArrayAdapter<ServiceSong> adapter;
    private String serviceName;
    private ListView songListView;
    private PresentationScreenService presentationScreenService;
    private ArrayList<ServiceSong> serviceSongs;
    private SongDao songDao;
    private Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        presentationScreenService = new PresentationScreenService(ServiceSongsActivity.this);
        Intent intent = getIntent();
        serviceName = intent.getStringExtra("serviceName");
        setActionBar();
        setSongListView();
        loadSongs();
    }

    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(serviceName);
    }

    private void setSongListView()
    {
        songListView = (ListView) findViewById(R.id.song_list_view);
    }

    private void loadSongs()
    {
        File serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(";");
        songDao = new SongDao(this);
        serviceSongs = new ArrayList<ServiceSong>();
        for (String title : propertyValues) {
            Song song = songDao.findContentsByTitle(title);
            serviceSongs.add(new ServiceSong(title, song));
        }
        adapter = new ServiceSongAdapter(ServiceSongsActivity.this, serviceSongs, serviceName);
        songListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        menu.getItem(0).setVisible(false);
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

    public void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
        if(state != null) {
            songListView.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
        state = songListView.onSaveInstanceState();
    }
}
