package org.worshipsongs.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.worshipsongs.adapter.CustomListViewAdapter;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.adapter.SongContentViewerPageAdapter;
import org.worshipsongs.component.HomeViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongContentViewActivity extends AppCompatActivity
{
    private UserPreferenceSettingService userPreferenceSettingService;
    private List<String> verseName;
    private List<String> verseContent;
    private List<String> contents = new ArrayList<>();
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
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(intent.getStringExtra("serviceName"));
        verseName = new ArrayList<String>();
        verseContent = new ArrayList<String>();
        verseName = intent.getStringArrayListExtra("verseName");
        verseContent = intent.getStringArrayListExtra("verseContent");
        Log.d(this.getClass().getName(), "Verse Name :" + verseName);
        Log.d(this.getClass().getName(), "Verse Content :" + verseName);
        if (verseName != null) {
            for (int i = 0; i < verseName.size(); i++) {
                Log.d(this.getClass().getName(), "customListViewAdapter Verse Content :" + verseContent.get(i));
               // customListViewAdapter.addItem(verseContent.get(i));
                contents.add(verseContent.get(i));
            }
        }
        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
            RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            //ContactAdapter ca = new ContactAdapter(createList(30));
            SongCardViewAdapter ca = new SongCardViewAdapter(contents, this);
            recList.setAdapter(ca);
        } else {
            actionBar.hide();
            // adapter = new HomeViewerPageAdapter(getChildFragmentManager(), titles);
            SongContentViewerPageAdapter songContentViewerPageAdapter =
                    new SongContentViewerPageAdapter(getSupportFragmentManager(), contents);
            // Assigning ViewPager View and setting the adapter
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(songContentViewerPageAdapter);
            // Assiging the Sliding Tab Layout View
            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
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
            tabs.setVisibility(View.INVISIBLE);
            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
        }
    }

//    private void initializeAdapter()
//    {
//        customListViewAdapter = new CustomListViewAdapter(this);
//        if (verseName != null) {
//            for (int i = 0; i < verseName.size(); i++) {
//                Log.d(this.getClass().getName(),"customListViewAdapter Verse Content :"+ verseContent.get(i));
//                customListViewAdapter.addItem(verseContent.get(i));
//            }
//        }
//        setListAdapter(customListViewAdapter);
//    }

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