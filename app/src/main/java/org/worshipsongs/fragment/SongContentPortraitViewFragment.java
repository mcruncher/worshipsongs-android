package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Madasamy
 * version: 1.0.0
 */
public class SongContentPortraitViewFragment extends Fragment
{
    private SongCardViewAdapter songCarViewAdapter;
    private WorshipSongApplication application = new WorshipSongApplication();
    private SongDao songDao = new SongDao(application.getContext());
    private UtilitiesService utilitiesService = new UtilitiesService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_portrait_view, container, false);
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        //  List<String> content = new ArrayList<>();
        Bundle bundle = getArguments();
        List<String> contents = songDao.findContentsByTitle(bundle.getString(CommonConstants.TITLE_KEY));
        songCarViewAdapter = new SongCardViewAdapter(contents, this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recList.setAdapter(songCarViewAdapter);
        return view;
    }
}
