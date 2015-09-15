package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.List;

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
        String title = bundle.getString(CommonConstants.TITLE_KEY);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setElevation(0);
//        actionBar.hide();
        ImageView imageView  =(ImageView)view.findViewById(R.id.back_navigation);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });

        TextView textView = (TextView)view.findViewById(R.id.song_title);
        textView.setText(title);

        List<String> contents = songDao.findContentsByTitle(title);
        songCarViewAdapter = new SongCardViewAdapter(contents, this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recList.setAdapter(songCarViewAdapter);
        return view;
    }
}
