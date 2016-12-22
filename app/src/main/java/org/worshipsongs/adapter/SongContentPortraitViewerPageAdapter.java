package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Madasamy
 * version : 2.1.0
 */
public class SongContentPortraitViewerPageAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<String> titles;
    private FragmentManager fragmentManager;

    public SongContentPortraitViewerPageAdapter(FragmentManager fragmentManager, ArrayList<String> titles)
    {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.i(this.getClass().getSimpleName(), "No of songs" + titles.size());
        String title = titles.get(position);
        SongContentPortraitViewFragment songContentPortraitViewFragment =  SongContentPortraitViewFragment.newInstance(title, titles);
        return songContentPortraitViewFragment;
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