package org.worshipsongs.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.fragment.AuthorsFragment;
import org.worshipsongs.fragment.ServicesFragment;
import org.worshipsongs.fragment.SongBookFragment;
import org.worshipsongs.fragment.SongsFragment;
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
        return getFragment(position);
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

    private Fragment getFragment(int position)
    {
        String title = titles.get(position);
        if (WorshipSongApplication.getContext().getString(R.string.titles).equalsIgnoreCase(title)) {
            SongsFragment songsFragment = SongsFragment.newInstance(null);
            songsFragment.setSongContentViewListener(songContentViewListener);
            return songsFragment;
        } else if ((WorshipSongApplication.getContext().getString(R.string.artists).equalsIgnoreCase(title))) {
            return AuthorsFragment.newInstance();
        } else if ((WorshipSongApplication.getContext().getString(R.string.categories).equalsIgnoreCase(title))) {
            return new TopicsFragment();
        } else if ((WorshipSongApplication.getContext().getString(R.string.song_books).equalsIgnoreCase(title))) {
            return SongBookFragment.newInstance();
        } else {
            return ServicesFragment.newInstance();
        }

    }
}

