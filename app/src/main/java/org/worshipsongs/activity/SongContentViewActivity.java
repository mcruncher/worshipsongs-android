package org.worshipsongs.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.CustomListViewAdapter;
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter;
import org.worshipsongs.adapter.SongContentPortraitViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;


/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongContentViewActivity extends AppCompatActivity
{
    private UserPreferenceSettingService userPreferenceSettingService;
    private TextView textView;
    private ActionBar actionBar;
    private CustomListViewAdapter customListViewAdapter;
    private boolean isSectionView = true;
    private boolean isTabView = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_content_view);
        userPreferenceSettingService = new UserPreferenceSettingService();
        if (userPreferenceSettingService.getKeepAwakeStatus()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (savedInstanceState != null) {
            isSectionView = savedInstanceState.getBoolean("isSectionView");
            isTabView = savedInstanceState.getBoolean("isTabView");
        }
        Intent intent = getIntent();
        actionBar = getSupportActionBar();
        actionBar.hide();

        ArrayList<String> songList = intent.getExtras().getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        int position = intent.getExtras().getInt(CommonConstants.POSITION_KEY);

        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
            SongContentPortraitViewerPageAdapter songContentLandScapeViewerPageAdapter =
                    new SongContentPortraitViewerPageAdapter(getSupportFragmentManager(), songList);
            // Assigning ViewPager View and setting the adapter
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(songContentLandScapeViewerPageAdapter);
            // Assiging the Sliding Tab Layout View
            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            //tabs.setVerticalScrollbarPosition();
            tabs.setDistributeEvenly(false);
            // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
            {
                @Override
                public int getIndicatorColor(int position)
                {
                    return getResources().getColor(android.R.color.background_dark);
                }
            });
            tabs.setVisibility(View.GONE);
            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            pager.setCurrentItem(position);
        } else {
            SongContentLandScapeViewerPageAdapter songContentLandScapeViewerPageAdapter =
                    new SongContentLandScapeViewerPageAdapter(getSupportFragmentManager(), songList);
            // Assigning ViewPager View and setting the adapter
            ViewPager pager = (ViewPager) findViewById(R.id.land_pager);
            pager.setAdapter(songContentLandScapeViewerPageAdapter);
            // Assiging the Sliding Tab Layout View
            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.land_tabs);
            //tabs.setVerticalScrollbarPosition();
            tabs.setDistributeEvenly(false);
            // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
            {
                @Override
                public int getIndicatorColor(int position)
                {
                    return getResources().getColor(android.R.color.background_dark);
                }
            });
            tabs.setVisibility(View.GONE);
            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            pager.setCurrentItem(position);
        }
    }

    public void onResume()
    {
        super.onResume();
        //  setListAdapter(customListViewAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //  outState.putBoolean("isSectionView", isSectionView);
        //outState.putBoolean("isTabView", isTabView);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
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