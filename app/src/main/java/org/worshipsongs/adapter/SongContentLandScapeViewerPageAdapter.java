package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.SongContentLandscapeViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentLandScapeViewerPageAdapter extends FragmentStatePagerAdapter
{
    private int orientationId;
    private List<String> titles;

    public SongContentLandScapeViewerPageAdapter(FragmentManager fragmentManager, ArrayList<String> titles)
    {
        super(fragmentManager);
        this.titles = titles;
        this.orientationId = orientationId;
    }


    @Override
    public Fragment getItem(int position)
    {
        Log.i(this.getClass().getSimpleName(), "No of songs" + titles.size());
        SongContentLandscapeViewFragment songContentLandscapeViewFragment = new SongContentLandscapeViewFragment();
        Bundle bundle = new Bundle();
        String title = titles.get(position);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        songContentLandscapeViewFragment.setArguments(bundle);
        return songContentLandscapeViewFragment;
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
