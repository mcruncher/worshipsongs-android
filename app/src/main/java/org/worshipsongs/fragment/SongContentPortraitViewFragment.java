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
        List<String> contents = getContents(bundle.getString(CommonConstants.TITLE_KEY));
        songCarViewAdapter = new SongCardViewAdapter(contents, this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recList.setAdapter(songCarViewAdapter);
        return view;
    }

    List<String> getContents(String title)
    {
        Log.d("Selected song:", title);
        Song song = songDao.getSongByTitle(title);
        String lyrics = song.getLyrics();
        List<String> contents = new ArrayList<>();
        List<Verse> verseList = utilitiesService.getVerse(lyrics);
        List<String> verseName = new ArrayList<String>();
        List<String> contentsByDefaultOrder = new ArrayList<String>();
        Map<String, String> verseDataMap = new HashMap<String, String>();
        for (Verse verses : verseList) {
            verseName.add(verses.getType() + verses.getLabel());
            contentsByDefaultOrder.add(verses.getContent());
            verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
        }
        List<String> contentsByVerseOrder = new ArrayList<String>();
        List<String> verseOrderList = new ArrayList<String>();
        String verseOrder = song.getVerseOrder();
        if (StringUtils.isNotBlank(verseOrder)) {
            verseOrderList = utilitiesService.getVerseByVerseOrder(verseOrder);
        }

        if (verseOrderList.size() > 0) {
            for (int i = 0; i < verseOrderList.size(); i++) {
                contentsByVerseOrder.add(verseDataMap.get(verseOrderList.get(i)));
            }
            contents.addAll(contentsByVerseOrder);
            Log.d(this.getClass().getName(), "Verse List data content :" + contentsByVerseOrder);
        } else {
            contents.addAll(contentsByDefaultOrder);
        }

        return contents;
    }
}
