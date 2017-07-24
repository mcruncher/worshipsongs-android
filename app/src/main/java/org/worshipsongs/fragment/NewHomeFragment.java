package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class NewHomeFragment extends Fragment implements SongContentViewListener
{
    private FrameLayout songContentFrameLayout;

    public static NewHomeFragment newInstance()
    {
        return new NewHomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.new_home_layout, container, false);
        setContentViewFragment(view);
        setTabsFragment();
        return view;
    }

    private void setTabsFragment()
    {
        HomeTabFragment homeTabFragment = HomeTabFragment.newInstance();
        if (songContentFrameLayout != null) {
            homeTabFragment.setSongContentViewListener(this);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.tabs_fragment, homeTabFragment).commit();
    }

    private void setContentViewFragment(View view)
    {
        songContentFrameLayout = (FrameLayout) view.findViewById(R.id.song_content_fragment);

    }

    @Override
    public void displayContent(String title, List<String> titleList, int position)
    {
        if (songContentFrameLayout != null) {
            SongContentPortraitViewFragment songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, new ArrayList<String>(titleList));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.song_content_fragment, songContentPortraitViewFragment).commit();
        }
    }

}
