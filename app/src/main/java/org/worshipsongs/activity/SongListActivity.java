package org.worshipsongs.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.FrameLayout;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.fragment.SongsFragment;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.PresentationScreenService;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Seenivasan, Madasamy
 * version: 2.1.0
 */
public class SongListActivity extends AppCompatActivity implements SongContentViewListener
{
    private PresentationScreenService presentationScreenService;
    private FrameLayout songContentFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        initSetUp();
        setContentViewFragment();
        setFragment();
    }

    private void initSetUp()
    {
        presentationScreenService = new PresentationScreenService(this);
        setActionBar();
    }

    private void setActionBar()
    {
        String title = getIntent().getStringExtra(CommonConstants.TITLE_KEY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }

    private void setContentViewFragment()
    {
        songContentFrameLayout = (FrameLayout) findViewById(R.id.song_content_fragment);
    }

    private void setFragment()
    {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(CommonConstants.TYPE, intent.getStringExtra(CommonConstants.TYPE));
        bundle.putInt(CommonConstants.ID, intent.getIntExtra(CommonConstants.ID, 0));
        SongsFragment songsFragment = SongsFragment.newInstance(bundle);
        if (songContentFrameLayout != null) {
            songsFragment.setSongContentViewListener(this);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.tabs_fragment, songsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    protected void onResume()
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
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }


}
