package org.worshipsongs.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter;
import org.worshipsongs.adapter.SongContentPortraitViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;

/**
 * @Author : Seenivasan, Madasamy
 * @Version : 1.0
 */
public class SongContentViewActivity extends AppCompatActivity
{
    private UserPreferenceSettingService userPreferenceSettingService;
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ArrayList<String> titleList = intent.getExtras().getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
            SongContentPortraitViewerPageAdapter songContentLandScapeViewerPageAdapter =
                    new SongContentPortraitViewerPageAdapter(getSupportFragmentManager(), titleList);
            // Assigning ViewPager View and setting the adapter
            final ViewPager pager = (ViewPager) findViewById(R.id.pager);
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
            pager.setCurrentItem(Setting.getInstance().getPosition());

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {
                    Setting.getInstance().setPosition(position);
                }

                @Override
                public void onPageSelected(int position)
                {

                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                }
            });
        } else {
            SongContentLandScapeViewerPageAdapter songContentLandScapeViewerPageAdapter =
                    new SongContentLandScapeViewerPageAdapter(getSupportFragmentManager(),
                            titleList.get(Setting.getInstance().getPosition()));
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
        }
    }

    public void onResume()
    {
        super.onResume();
        //  setListAdapter(customListViewAdapter);
    }


    @Override
    protected void onPause()
    {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
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