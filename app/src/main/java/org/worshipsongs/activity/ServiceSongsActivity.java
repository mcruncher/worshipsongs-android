package org.worshipsongs.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.ServiceSongAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.fragment.ServiceSongsFragment;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Seenivasan, Madasamy
 * version 1.0.0
 */
public class ServiceSongsActivity extends AppCompatActivity implements SongContentViewListener
{
    private FrameLayout songContentFrameLayout;
    private PresentationScreenService presentationScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        presentationScreenService = new PresentationScreenService(ServiceSongsActivity.this);
        setActionBar();
        setContentViewFragment();
        setTabsFragment();
    }

    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra(CommonConstants.SERVICE_NAME_KEY));
    }

    private void setContentViewFragment()
    {
        songContentFrameLayout = (FrameLayout) findViewById(R.id.song_content_fragment);
    }

    private void setTabsFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ServiceSongsFragment exisitingServiceSongsFragment = (ServiceSongsFragment) fragmentManager.findFragmentByTag(ServiceSongsFragment.class.getSimpleName());
        if (exisitingServiceSongsFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.SERVICE_NAME_KEY, getIntent().getStringExtra(CommonConstants.SERVICE_NAME_KEY));
            ServiceSongsFragment serviceSongsFragment = ServiceSongsFragment.newInstance(bundle);
            serviceSongsFragment.setSongContentViewListener(this);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.tabs_fragment, serviceSongsFragment, HomeTabFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void displayContent(String title, List<String> titleList, int position)
    {
        if (songContentFrameLayout != null) {
            SongContentPortraitViewFragment songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, new ArrayList<String>(titleList));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.action_bar_menu, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(true);
//        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
//        Drawable drawable = image.getDrawable();
//        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
//        {
//            @Override
//            public boolean onQueryTextChange(String newText)
//            {
//                adapter.getFilter().filter(newText);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query)
//            {
//                adapter.getFilter().filter(query);
//                return true;
//            }
//        };
//        searchView.setOnQueryTextListener(textChangeListener);
//        menu.getItem(0).setVisible(false);
//        return super.onCreateOptionsMenu(menu);
//    }

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
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }


}
