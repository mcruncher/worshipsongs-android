package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.worshipsongs.fragment.SongContentFullViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentViewerPageAdapter extends FragmentStatePagerAdapter
{
    private List<String> contents;
    private List<ArrayList<String>> songList;

//    public SongContentViewerPageAdapter(FragmentManager fragmentManager, List<String> contents)
//    {
//        super(fragmentManager);
//        this.contents = contents;
//    }

    public SongContentViewerPageAdapter(FragmentManager fragmentManager, List<ArrayList<String>> songList)
    {
        super(fragmentManager);
        this.songList = songList;
    }


    @Override
    public Fragment getItem(int position)
    {
        SongContentFullViewFragment songContentFullViewFragment = new SongContentFullViewFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("content", songList.get(position));
        songContentFullViewFragment.setArguments(bundle);
        return songContentFullViewFragment;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {

        return "";
    }

    @Override
    public int getCount()
    {

        return songList.size();
    }
}
