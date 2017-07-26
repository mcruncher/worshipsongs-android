package org.worshipsongs.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.worshipsongs.fragment.AuthorsFragment;
import org.worshipsongs.fragment.SongsFragment;
import org.worshipsongs.fragment.ServicesFragment;
import org.worshipsongs.fragment.TopicsFragment;
import org.worshipsongs.listener.SongContentViewListener;

import java.util.List;

/**
 * Author: madasamy.
 * version: 1.0.0
 */
public class HomeViewerPageAdapter extends FragmentPagerAdapter
{
    private List<String> titles;
    private SongContentViewListener songContentViewListener;

    public HomeViewerPageAdapter(FragmentManager fragmentManager, List<String> titles, SongContentViewListener songContentViewListener)
    {
        super(fragmentManager);
        this.titles = titles;
        this.songContentViewListener = songContentViewListener;
    }

    @Override
    public Fragment getItem(int position)
    {

        switch (position) {
            case 0:
                SongsFragment songsFragment = SongsFragment.newInstance(null);
                songsFragment.setSongContentViewListener(songContentViewListener);
                return songsFragment;
            case 1:
                return new AuthorsFragment();
            case 2:
                return new TopicsFragment();
            case 3:
                return new ServicesFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles.get(position);
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }
}

