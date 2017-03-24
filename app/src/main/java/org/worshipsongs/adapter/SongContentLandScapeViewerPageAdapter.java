package org.worshipsongs.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.fragment.SongContentLandscapeViewFragment;
import org.worshipsongs.service.AuthorService;
import org.worshipsongs.service.IAuthorService;

import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentLandScapeViewerPageAdapter extends FragmentStatePagerAdapter
{
    private String title;

    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private IAuthorService authorService = new AuthorService(WorshipSongApplication.getContext());
    private List<String> contents;
    private String authorName;
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
        authorName = authorService.findNameByTitle(title);
    }

    @Override
    public Fragment getItem(int position)
    {
        SongContentLandscapeViewFragment songContentLandscapeViewFragment = new SongContentLandscapeViewFragment();
        Bundle bundle = new Bundle();
        String content = contents.get(position);
        bundle.putString("content", content);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        bundle.putString("authorName", authorName);
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
