package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.CommonConstants;
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
    private Bundle bundle;

    public SongContentPortraitViewerPageAdapter(FragmentManager fragmentManager, Bundle bundle, PresentationScreenService presentationScreenService)
    {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.presentationScreenService = presentationScreenService;
        this.titles = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.i(this.getClass().getSimpleName(), "No of songs" + titles.size());
        String title = titles.get(position);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        SongContentPortraitViewFragment songContentPortraitViewFragment =  SongContentPortraitViewFragment.newInstance(bundle);
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