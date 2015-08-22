package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.worshipsongs.fragment.SongContentFullViewFragment;

import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentViewerPageAdapter extends FragmentStatePagerAdapter
{
    private  List<String> contents;

    public SongContentViewerPageAdapter(FragmentManager fragmentManager, List<String> contents)
    {
        super(fragmentManager);
        this.contents = contents;
    }

    @Override
    public Fragment getItem(int position)
    {
        SongContentFullViewFragment songContentFullViewFragment = new SongContentFullViewFragment();
        Bundle  bundle = new Bundle();
        bundle.putString("content", contents.get(position));
        songContentFullViewFragment.setArguments(bundle);
        //songContentFullViewFragment.setText(contents.get(0));
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

        return contents.size();
    }
}
