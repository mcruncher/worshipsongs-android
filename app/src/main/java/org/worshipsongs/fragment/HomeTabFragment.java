package org.worshipsongs.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.component.HomeViewerPageAdapter;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.FragmentRegistry;

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
    private List<String> titles;

    public static HomeTabFragment newInstance()
    {
        return new HomeTabFragment();
    }

    @SuppressLint("ShowToast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.home_tab_layout, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        titles = fragmentRegistry.getTitles(getActivity());
        HomeViewerPageAdapter adapter = new HomeViewerPageAdapter(getChildFragmentManager(), getActivity(), titles, songContentViewListener);
        adapter.notifyDataSetChanged();

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        tabLayout.setBackgroundColor(typedValue.data);
        tabLayout.setupWithViewPager(pager);
        setTabIcon(tabLayout);
        // setSelectedTab(pager);
        displayPlayListTab(pager);
        return view;
    }

    private void setTabIcon(TabLayout tabLayout)
    {
        for (int i = 0; i < titles.size(); i++) {
            int drawable = getActivity().getResources().getIdentifier("ic_"+titles.get(i),
                    "drawable", WorshipSongApplication.getContext().getPackageName());
            tabLayout.getTabAt(i).setIcon(getResources().getDrawable(drawable));
        }
    }

    private void displayPlayListTab(ViewPager pager)
    {
        if (getArguments() != null && getArguments().getInt(CommonConstants.FAVOURITES_KEY) > 0) {
            List<String> titles = fragmentRegistry.getTitles(getActivity());
            if (titles.contains("playlists")) {
                pager.setCurrentItem(titles.indexOf("playlists"));
            }
        } else if (getArguments() != null && getArguments().getInt(CommonConstants.FAVOURITES_KEY) == 0) {
            Toast.makeText(getActivity(), R.string.message_songs_not_existing, Toast.LENGTH_LONG).show();
        }
    }

    public void setSongContentViewListener(SongContentViewListener songContentViewListener)
    {
        this.songContentViewListener = songContentViewListener;
    }
}