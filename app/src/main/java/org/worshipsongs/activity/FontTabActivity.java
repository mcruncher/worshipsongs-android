package org.worshipsongs.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.worshipsongs.adapter.FontViewerPageAdapter;
import org.worshipsongs.component.HomeViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.worship.R;

import java.util.Arrays;
import java.util.List;

/**
 * Author: madasamy
 * version: 2.1.0
 */
public class FontTabActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_tab_activity);


        List<String> titles = Arrays.asList(getString(R.string.size), getString(R.string.style));
        FontViewerPageAdapter adapter = new FontViewerPageAdapter(getSupportFragmentManager(), titles);
        adapter.notifyDataSetChanged();
        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(R.id.font_pager);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.font_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(R.color.bright_foreground_material_dark);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setViewPager(pager);
        ActionBar supportActionBar = getSupportActionBar();
        Log.i(this.getClass().getSimpleName(), "Support action bar " + supportActionBar);
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }
}
