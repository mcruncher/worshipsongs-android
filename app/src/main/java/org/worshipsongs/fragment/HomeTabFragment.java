package org.worshipsongs.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.activity.NavigationDrawerActivity;
import org.worshipsongs.component.HomeViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.FragmentRegistry;
import org.worshipsongs.registry.ITabFragment;

import java.util.List;

/**
 * author:Seenivasan, Madasamy
 * version:2.1.0
 */
public class HomeTabFragment extends Fragment
{
    private SongContentViewListener songContentViewListener;
    private SharedPreferences preferences;
    private FragmentRegistry fragmentRegistry = new FragmentRegistry();

    public static HomeTabFragment newInstance()
    {
        return new HomeTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.home_tab_layout, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Creating The HomeViewerPageAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.ome
        Log.i(this.getClass().getSimpleName(), "Preparing to load home view fragment");

        HomeViewerPageAdapter adapter = new HomeViewerPageAdapter(getChildFragmentManager(), getActivity(), songContentViewListener);
        adapter.notifyDataSetChanged();

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);


        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(android.R.color.white);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        if (getArguments() != null && getArguments().getString(CommonConstants.FAVOURITES_KEY) != null) {
            List<String> titles = fragmentRegistry.getTitles(getActivity());
            if (titles.contains("playlists")) {
                pager.setCurrentItem(titles.indexOf("playlists"));
            }
        }
        return view;
    }

    public void setSongContentViewListener(SongContentViewListener songContentViewListener)
    {
        this.songContentViewListener = songContentViewListener;
    }
}