package org.worshipsongs.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.worshipsongs.fragment.AuthorListFragment;
import org.worshipsongs.fragment.ServiceListFragment;
import org.worshipsongs.fragment.SongBookListFragment;
import org.worshipsongs.fragment.SongsListFragment;

import java.util.List;

/**
 * Created by madasamy on 8/16/15.
 */
public class HomeViewerPageAdapter extends FragmentStatePagerAdapter
{

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when HomeViewerPageAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the HomeViewerPageAdapter is created

    private List<String> titles;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public HomeViewerPageAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb)
    {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    public HomeViewerPageAdapter(FragmentManager fragmentManager, List<String> titles)
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
                return new SongsListFragment();
            case 1:
                return new AuthorListFragment();
            case 2:
                return new SongBookListFragment();
            case 3:
                return new ServiceListFragment();
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

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

