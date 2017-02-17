package org.worshipsongs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.service.PresentationScreenService;

import java.util.ArrayList;

/**
 * author : Madasamy
 * version : 2.1.0
 */
public class SongContentPortraitViewerPageAdapter extends FragmentStatePagerAdapter
{
    private PresentationScreenService presentationScreenService;
    private ArrayList<String> titles;
    private FragmentManager fragmentManager;

    public SongContentPortraitViewerPageAdapter(FragmentManager fragmentManager, ArrayList<String> titles, PresentationScreenService presentationScreenService)
    {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.presentationScreenService = presentationScreenService;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.i(this.getClass().getSimpleName(), "No of songs" + titles.size());
        String title = titles.get(position);
        SongContentPortraitViewFragment songContentPortraitViewFragment =  SongContentPortraitViewFragment.newInstance(title, titles);
        songContentPortraitViewFragment.setPresentationScreenService(presentationScreenService);
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