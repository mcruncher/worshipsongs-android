package org.worshipsongs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.worshipsongs.activity.FontSizeFragment;
import org.worshipsongs.activity.FontStyleFragment;
import org.worshipsongs.fragment.AuthorListFragment;
import org.worshipsongs.fragment.ServiceListFragment;
import org.worshipsongs.fragment.SongBookListFragment;
import org.worshipsongs.fragment.SongsListFragment;

import java.util.List;

/**
 * Author: madasamy .
 * version : 2.1.0
 */
public class FontViewerPageAdapter extends FragmentPagerAdapter
{
    private List<String> titles;


    public FontViewerPageAdapter(FragmentManager fragmentManager, List<String> titles)
    {
        super(fragmentManager);
        this.titles = titles;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position)
    {

        switch (position) {
            case 0:
                return new FontSizeFragment();
            case 1:
                return new FontStyleFragment();

        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles.get(position);
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount()
    {
        return titles.size();
    }
}
