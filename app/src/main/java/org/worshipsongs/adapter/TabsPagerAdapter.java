package org.worshipsongs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.worshipsongs.fragment.NewAuthorListFragment;
import org.worshipsongs.fragment.NewSongBookListFragment;
import org.worshipsongs.fragment.ServiceListFragment;
import org.worshipsongs.fragment.SongsListFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * The total number of tabs
     */
    private int totalTabs;

    public TabsPagerAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new SongsListFragment();
            case 1:
                return new NewAuthorListFragment();
            case 2:
                return new NewSongBookListFragment();
            case 3:
                return new ServiceListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
