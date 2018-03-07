package org.worshipsongs.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.fragment.FavouriteSongsFragment;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.fragment.ServiceSongsFragment;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.PresentationScreenService;

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
       // ServiceSongsFragment exisitingServiceSongsFragment = (ServiceSongsFragment) fragmentManager.findFragmentByTag(ServiceSongsFragment.class.getSimpleName());
        FavouriteSongsFragment existingServiceSongsFragment = (FavouriteSongsFragment)fragmentManager.findFragmentByTag(FavouriteSongsFragment.class.getSimpleName());
        if (existingServiceSongsFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.SERVICE_NAME_KEY, getIntent().getStringExtra(CommonConstants.SERVICE_NAME_KEY));
            FavouriteSongsFragment serviceSongsFragment = FavouriteSongsFragment.newInstance(bundle);
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
            ArrayList<String> titles = new ArrayList<>();
            titles.add(title);
            SongContentPortraitViewFragment songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, titles);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
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
