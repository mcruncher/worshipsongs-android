package org.worshipsongs.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.NewSongsFragment;
import org.worshipsongs.fragment.SongsListFragment;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.worship.R;

/**
 * author: Seenivasan, Madasamy
 * version: 2.1.0
 */
public class SongListActivity extends AppCompatActivity
{
    private PresentationScreenService presentationScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        initSetUp();
        setListView();
        setFragment(savedInstanceState);
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

    private void setListView()
    {
        ListView songListView = (ListView) findViewById(R.id.song_list_view);
        songListView.setVisibility(View.GONE);
    }

    private void setFragment(Bundle bundle)
    {
        FragmentActivity fragmentActivity = (FragmentActivity) this;
        Intent intent = getIntent();
//        String type = intent.getStringExtra(CommonConstants.TYPE);
//        int id = intent.getIntExtra(CommonConstants.ID, 0);
        //SongsListFragment songsListFragment = SongsListFragment.newInstance(bundle);
        Bundle bundle1 = new Bundle();
        bundle1.putString(CommonConstants.TYPE, intent.getStringExtra(CommonConstants.TYPE));
        bundle1.putInt(CommonConstants.ID, intent.getIntExtra(CommonConstants.ID, 0));
        NewSongsFragment songsListFragment = NewSongsFragment.newInstance(bundle1);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.song_list_fragment, songsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
