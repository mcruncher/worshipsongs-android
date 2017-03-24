package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.fragment.SongContentLandscapeViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentLandScapeViewerPageAdapter extends FragmentStatePagerAdapter
{
    private String title;
    private WorshipSongApplication application = new WorshipSongApplication();
    private SongDao songDao = new SongDao(application.getContext());
    private AuthorSongDao authorSongDao = new AuthorSongDao(application.getContext());
    private List<String> contents;
    private AuthorSong authorSong;
    private Song song;


    public SongContentLandScapeViewerPageAdapter(FragmentManager fragmentManager, String title)
    {
        super(fragmentManager);
        this.title = title;
        initSetUp();
    }

    public void initSetUp()
    {
        song = songDao.findContentsByTitle(title);
        contents = song.getContents();
        authorSong = authorSongDao.findByTitle(title);
    }

    @Override
    public Fragment getItem(int position)
    {
        SongContentLandscapeViewFragment songContentLandscapeViewFragment = new SongContentLandscapeViewFragment();
        Bundle bundle = new Bundle();
        String content = contents.get(position);
        bundle.putString("content", content);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        bundle.putString("authorName", authorSong.getAuthor().getName());
        bundle.putString("position", String.valueOf(position));
        bundle.putString("size", String.valueOf(contents.size()));
        bundle.putString("chord", song.getChord());
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
        return contents.size();
    }
}
