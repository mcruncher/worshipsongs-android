package org.worshipsongs.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Madasamy
 * version: 1.0.0
 */
public class SongContentPortraitViewFragment extends Fragment
{
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";
    private String title;
    private ArrayList<String> tilteList;
    private int millis;
    private boolean isFullscreen;
    private boolean playVideoStatus;


    private YouTubePlayer mPlayer;

    private SongCardViewAdapter songCarViewAdapter;
    private WorshipSongApplication application = new WorshipSongApplication();
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService = new CustomTagColorService();
    private SongDao songDao = new SongDao(application.getContext());
    private UtilitiesService utilitiesService = new UtilitiesService();
    private SongListAdapterService songListAdapterService;

    public static SongContentPortraitViewFragment newInstance(String title, ArrayList<String> titles) {
        SongContentPortraitViewFragment songContentPortraitViewFragment = new SongContentPortraitViewFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles);
        bundle.putString(CommonConstants.TITLE_KEY, title);
        songContentPortraitViewFragment.setArguments(bundle);
        return songContentPortraitViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = (View) inflater.inflate(R.layout.song_content_portrait_view, container, false);
        showStatusBar();
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        Song song = songDao.findContentsByTitle(title);
        //setYouTubeView(view);
        setRecyclerView(view, song);
        setTitleTextView(view);
        setOptionsImageView(view, song.getContents());
        setFloatingButton(view, song.getUrlKey());
        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.i(this.getClass().getSimpleName(), "Position " + tilteList.indexOf(title));
                int position = tilteList.indexOf(title);
                Setting.getInstance().setPosition(position);
                return true;
            }
        });
        Log.i(this.getClass().getSimpleName(), "Video status:" + playVideoStatus);
        return view;
    }



    private void setRecyclerView(View view, Song song)
    {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recyclerView.setHasFixedSize(true);
        preferenceSettingService = new UserPreferenceSettingService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        songCarViewAdapter = new SongCardViewAdapter(song.getContents(), this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(songCarViewAdapter);
    }

    private void setTitleTextView(View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.song_title);
        textView.setText(title);
    }

    private void setOptionsImageView(View view, final List<String> contents)
    {
        ImageView imageView = (ImageView) view.findViewById(R.id.back_navigation);
        ImageView optionMenu = (ImageView) view.findViewById(R.id.optionMenu);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StringBuilder builder = new StringBuilder();
                builder.append(title).append("\n").append("\n");
                for (String content : contents) {
                    builder.append(customTagColorService.getFormattedLines(content));
                }
                builder.append(getActivity().getString(R.string.share_info));
                songListAdapterService = new SongListAdapterService();
                songListAdapterService.showPopupmenu(view, title, getFragmentManager(), false);
            }
        });
    }



    private void showStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setFloatingButton(View view, final String urrlKey)
    {
        playVideoStatus = preferenceSettingService.getPlayVideoStatus();
        FloatingActionButton playSongFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.play_song_fab);
        if (urrlKey != null && urrlKey.length() > 0 && playVideoStatus == true) {
            playSongFloatingActionButton.setVisibility(View.VISIBLE);
            playSongFloatingActionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showYouTube(urrlKey);
                }
            });
        }
    }

    private void showYouTube(String urlKey)
    {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(getActivity(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        youTubeIntent.putExtra("title", title);
        getActivity().startActivity(youTubeIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, mPlayer.getCurrentTimeMillis());
            Log.i(this.getClass().getSimpleName(), "Video duration: " + mPlayer.getCurrentTimeMillis());
        }
    }

}