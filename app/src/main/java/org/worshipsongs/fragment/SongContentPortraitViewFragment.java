package org.worshipsongs.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.youtube.player.YouTubePlayer;

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
    // private boolean playVideoStatus;
    private YouTubePlayer youTubePlayer;
    private SongCardViewAdapter songCarViewAdapter;
    private UserPreferenceSettingService preferenceSettingService;
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());

    private SongListAdapterService songListAdapterService;
    private FloatingActionsMenu floatingActionMenu;
    // private FloatingActionButton presentSongFloatingButton;
//    private FloatingActionButton hideSongFloatingButton;
   // private FloatingActionButton presentSongFloatingMenuButton;
   // private FloatingActionButton hideSongFloatingMenuButton;
    private SharedPreferences preferences;


    public static SongContentPortraitViewFragment newInstance(String title, ArrayList<String> titles)
    {
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
        initSetUp();
        Song song = songDao.findContentsByTitle(title);
        //setYouTubeView(view);
        setBackImageView(view);
        setTitleTextView(view);
        setOptionsImageView(view, song.getContents());
        setRecyclerView(view, song);
        //setPlaySongFloatingMenuButton(view, song.getUrlKey());
        setFloatingActionMenu(view, song);
        view.setOnTouchListener(new SongContentPortraitViewTouchListener());
        return view;
    }

    private void initSetUp()
    {
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    }


    private void setRecyclerView(View view, Song song)
    {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recyclerView.setHasFixedSize(true);
        preferenceSettingService = new UserPreferenceSettingService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        songCarViewAdapter = new SongCardViewAdapter(song, this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(songCarViewAdapter);
    }

    private void setTitleTextView(View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.song_title);
        textView.setText(title);
    }

    private void setBackImageView(View view)
    {
        ImageView imageView = (ImageView) view.findViewById(R.id.back_navigation);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });
    }

    private void setOptionsImageView(View view, final List<String> contents)
    {
        ImageView optionMenu = (ImageView) view.findViewById(R.id.optionMenu);
        optionMenu.setOnClickListener(new OptionsImageClickListener(contents));
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

    private void setFloatingActionMenu(View view, Song song)
    {
        floatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_action_menu);
        if (isPlayVideo(song.getUrlKey())) {
            floatingActionMenu.setVisibility(View.VISIBLE);
            setPlaySongFloatingMenuButton(view, song.getUrlKey());
            setPresentSongFloatingMenuButton(view);
        } else {
            floatingActionMenu.setVisibility(View.GONE);
            setPresentSongFloatingButton(view);
        }
    }

    private void setPlaySongFloatingMenuButton(View view, final String urrlKey)
    {
        FloatingActionButton playSongFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.play_song_floating_menu_button);
        if (isPlayVideo(urrlKey)) {
            playSongFloatingActionButton.setVisibility(View.VISIBLE);
            playSongFloatingActionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showYouTube(urrlKey);
                    if (floatingActionMenu.isExpanded()) {
                        floatingActionMenu.collapse();
                    }
                }
            });
        }
    }

    private void setPresentSongFloatingMenuButton(View view)
    {
        final FloatingActionButton  presentSongFloatingMenuButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_menu_button);
        presentSongFloatingMenuButton.setVisibility(View.VISIBLE);
        final boolean presentSong = preferences.getBoolean("presentSong", true);
        presentSongFloatingMenuButton.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view)
            {
                if (floatingActionMenu.isExpanded()) {
                    floatingActionMenu.collapse();
                }
                if (presentSong) {
                    songCarViewAdapter.showPresentation(0);
                    preferences.edit().putBoolean("presentSong", false).apply();
                } else {
                    songCarViewAdapter.hidePresentation(Setting.getInstance().getDisplay());
                    preferences.edit().putBoolean("presentSong", true).apply();
                }
            }
        });
    }

    private void setPresentSongFloatingButton(View view)
    {
        final FloatingActionButton presentSongFloatingButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_button);
        presentSongFloatingButton.setVisibility(View.VISIBLE);
        presentSongFloatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("SongContentPortraitView", "Present song");
                boolean presentSong = preferences.getBoolean("presentSong", true);
                Log.i(this.getClass().getSimpleName(), "Present song ?"+presentSong);
                if (presentSong) {
                    songCarViewAdapter.showPresentation(0);
                    preferences.edit().putBoolean("presentSong", false).apply();
                } else {
                    songCarViewAdapter.hidePresentation(Setting.getInstance().getDisplay());
                    preferences.edit().putBoolean("presentSong", true).apply();
                }
            }
        });
    }
    
    private boolean isPlayVideo(String urrlKey)
    {
        boolean playVideoStatus = preferenceSettingService.getPlayVideoStatus();
        return urrlKey != null && urrlKey.length() > 0 && playVideoStatus;
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
        if (youTubePlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, youTubePlayer.getCurrentTimeMillis());
            Log.i(this.getClass().getSimpleName(), "Video duration: " + youTubePlayer.getCurrentTimeMillis());
        }
    }

    private class SongContentPortraitViewTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            Log.i(this.getClass().getSimpleName(), "Position " + tilteList.indexOf(title));
            int position = tilteList.indexOf(title);
            Setting.getInstance().setPosition(position);
            return true;
        }
    }

    private class OptionsImageClickListener implements View.OnClickListener
    {
        private List<String> contents;

        OptionsImageClickListener(List<String> contents)
        {
            this.contents = contents;
        }

        @Override
        public void onClick(View view)
        {
            songListAdapterService = new SongListAdapterService();
            songListAdapterService.showPopupmenu(view, title, getFragmentManager(), false);
        }
    }

}