package org.worshipsongs.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;

import org.worshipsongs.adapter.TabsPagerAdapter;
import org.worshipsongs.worship.R;

/**
 * Created by Seenivasan on 4/5/2015.
 */
public class TabFragment extends Fragment implements ActionBar.TabListener {

    private TabHost tabHost;
    // Tab titles
    private String[] tabsTitles = {"Songs", "Artists", "Albums", "Playlists"};
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private DrawerLayout FragentLayout;
    private HorizontalScrollView horizontalScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragentLayout = (DrawerLayout) inflater.inflate(R.layout.activity_main_drawer, container, false);
        tabHost = (TabHost) FragentLayout.findViewById(android.R.id.tabhost);
        viewPager = (ViewPager) FragentLayout.findViewById(R.id.pager);
        tabHost.setup();
        setRetainInstance(true);
        for (int i = 0; i < tabsTitles.length; i++) {
            String tabName = tabsTitles[i];
            TabHost.TabSpec spec = tabHost.newTabSpec(tabName);
            spec.setContent(R.id.fakeTabContent);
            spec.setIndicator(tabName);
            tabHost.addTab(spec);
        }
        mAdapter = new TabsPagerAdapter(getChildFragmentManager(), tabsTitles.length);
        viewPager.setAdapter(mAdapter);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < tabsTitles.length; i++) {
                    if (tabId.equals(tabsTitles[i])) {
                        viewPager.setCurrentItem(i);
                        break;
                    }
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                SwipeRefreshLayout.OnRefreshListener fragment = (SwipeRefreshLayout.OnRefreshListener)
                        mAdapter.instantiateItem(viewPager, position);
                if (fragment != null) {
                    fragment.onRefresh();
                }
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        return FragentLayout;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
        SwipeRefreshLayout.OnRefreshListener fragment = (SwipeRefreshLayout.OnRefreshListener)
                mAdapter.instantiateItem(viewPager, tab.getPosition());
        if (fragment != null) {
            fragment.onRefresh();
        }
        tabHost.setCurrentTab(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
