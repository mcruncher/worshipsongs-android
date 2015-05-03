package org.worshipsongs.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.worshipsongs.fragment.AuthorListFragment;
import org.worshipsongs.fragment.ServiceListFragment;
import org.worshipsongs.fragment.SongBookListFragment;
import org.worshipsongs.fragment.SongsListFragment;

/**
 * Created by Seenivasan on 4/26/2015.
 */
public class FontTabPageAdapter extends FragmentPagerAdapter{

    /**
     * The total number of tabs
     */
    private int totalTabs;

    public FontTabPageAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int index) {
        switch(index) {
            case 0:
                return new FontSizeFragment();
            case 1:
                return new FontStyleFragment();
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
