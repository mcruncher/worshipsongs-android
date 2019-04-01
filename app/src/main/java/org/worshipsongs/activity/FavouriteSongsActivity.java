package org.worshipsongs.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.fragment.FavouriteSongsFragment;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.FavouriteService;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.service.SongService;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Seenivasan, Madasamy
 * version 1.0.0
 */
public class FavouriteSongsActivity extends AbstractAppCompactActivity implements SongContentViewListener
{
    private FrameLayout songContentFrameLayout;
    private PresentationScreenService presentationScreenService;
    private SharedPreferences preferences;
    private FavouriteService favouriteService;
    private SongService songService;
    private String favouriteName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        presentationScreenService = new PresentationScreenService(FavouriteSongsActivity.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(FavouriteSongsActivity.this);
        favouriteService = new FavouriteService();
        songService = new SongService(FavouriteSongsActivity.this);
        setActionBar();
        setContentViewFragment();
        setTabsFragment();
        displayHelpActivity();
    }

    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getFavouriteName());
    }

    private void setContentViewFragment()
    {
        songContentFrameLayout = (FrameLayout) findViewById(R.id.song_content_fragment);
    }

    private void setTabsFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FavouriteSongsFragment existingServiceSongsFragment = (FavouriteSongsFragment)
                fragmentManager.findFragmentByTag(FavouriteSongsFragment.class.getSimpleName());
        if (existingServiceSongsFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.SERVICE_NAME_KEY, getFavouriteName());
            FavouriteSongsFragment serviceSongsFragment = FavouriteSongsFragment.newInstance(bundle);
            serviceSongsFragment.setSongContentViewListener(this);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.tabs_fragment, serviceSongsFragment, HomeTabFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private String getFavouriteName()
    {
        return favouriteName != null ? favouriteName : getIntent().getStringExtra(CommonConstants.SERVICE_NAME_KEY);
    }

    private void displayHelpActivity()
    {
        if (!preferences.getBoolean(CommonConstants.DISPLAY_FAVOURITE_HELP_ACTIVITY, false)) {
            startActivity(new Intent(this, FavouriteSongsHelpActivity.class));
        }
    }

    @Override
    public void displayContent(String title, List<String> titleList, int position)
    {
        if (songContentFrameLayout != null) {
            ArrayList<String> titles = new ArrayList<>();
            titles.add(title);
            SongContentPortraitViewFragment songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, titles);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
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
