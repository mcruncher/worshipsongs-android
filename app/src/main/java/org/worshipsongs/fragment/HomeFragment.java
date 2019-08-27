package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.NavigationDrawerActivity;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.R;
import org.worshipsongs.registry.FragmentRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class HomeFragment extends Fragment implements SongContentViewListener
{
    private FrameLayout songContentFrameLayout;
    private FragmentRegistry fragmentRegistry = new FragmentRegistry();

    public static HomeFragment newInstance()
    {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.home_layout, container, false);
        setContentViewFragment(view);
        setTabsFragment();
        return view;
    }

    private void setTabsFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        HomeTabFragment existingHomeTabFragment = (HomeTabFragment) fragmentManager.findFragmentByTag(HomeTabFragment.class.getSimpleName());
        if (isNewTabSelected(existingHomeTabFragment)) {
            HomeTabFragment homeTabFragment = HomeTabFragment.newInstance();
            homeTabFragment.setArguments(getArguments());
            if (songContentFrameLayout != null) {
                homeTabFragment.setSongContentViewListener(this);
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.tabs_fragment, homeTabFragment, HomeTabFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    boolean isNewTabSelected(HomeTabFragment homeTabFragment)
    {
        if (homeTabFragment != null) {
            ViewPager viewPager = homeTabFragment.getView().findViewById(R.id.pager);
            int existingCurrentItem = viewPager.getCurrentItem();
            if(getArguments() !=null && getArguments().containsKey(CommonConstants.TAB_SELECTED_ITEM_ID)) {
                return getArguments().getInt(CommonConstants.TAB_SELECTED_ITEM_ID) != existingCurrentItem;
            }
        }
        return true;
    }

    private void setContentViewFragment(View view)
    {
        songContentFrameLayout = view.findViewById(R.id.song_content_fragment);
    }

    @Override
    public void displayContent(String title, List<String> titleList, int position)
    {
        if (songContentFrameLayout != null) {
            SongContentPortraitViewFragment songContentPortraitViewFragment =
                    SongContentPortraitViewFragment.newInstance(title,
                            new ArrayList<String>(titleList));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
    }


}
