package org.worshipsongs.component;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.fragment.SongsFragment;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.FragmentRegistry;
import org.worshipsongs.registry.ITabFragment;

import java.util.List;

/**
 * Author: madasamy.
 * version: 1.0.0
 */
public class HomeViewerPageAdapter extends FragmentPagerAdapter
{
    private static final String DEF_TYPE = "string";
    private Activity activity;
    private List<String> titles;
    private SongContentViewListener songContentViewListener;
    private FragmentRegistry fragmentRegistry = new FragmentRegistry();

    public HomeViewerPageAdapter(FragmentManager fragmentManager, Activity activity,
                                 List<String> titles, SongContentViewListener songContentViewListener)
    {
        super(fragmentManager);
        this.titles = titles;
        this.activity = activity;
        this.songContentViewListener = songContentViewListener;
    }

    @Override
    public Fragment getItem(int position)
    {
        ITabFragment fragment = fragmentRegistry.findByTitle(activity, titles.get(position));
        if (fragment != null) {
            fragment.setListenerAndBundle(songContentViewListener, null);
            return (Fragment) fragment;
        } else {
            return SongsFragment.newInstance(null);
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return "";
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }

}

