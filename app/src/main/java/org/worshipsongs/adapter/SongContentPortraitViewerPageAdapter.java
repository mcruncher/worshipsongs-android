package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Madasamy
 * version : 2.1.0
 */
public class SongContentPortraitViewerPageAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<String> titles;

    public SongContentPortraitViewerPageAdapter(FragmentManager fragmentManager, ArrayList<String> titles)
    {
        super(fragmentManager);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.i(this.getClass().getSimpleName(), "No of songs" + titles.size());
        SongContentPortraitViewFragment songContentPortraitViewFragment = new SongContentPortraitViewFragment();
        Bundle bundle = new Bundle();
        String title = titles.get(position);
        bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        songContentPortraitViewFragment.setArguments(bundle);
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