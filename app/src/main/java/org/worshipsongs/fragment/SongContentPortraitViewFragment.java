package org.worshipsongs.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.DefaultRemotePresentation;
import org.worshipsongs.dialog.RemoteSongPresentation;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Set;

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
    private YouTubePlayer youTubePlayer;
    private SongCardViewAdapter songCarViewAdapter;
    private UserPreferenceSettingService preferenceSettingService;
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private SongListAdapterService songListAdapterService;
    private FloatingActionsMenu floatingActionMenu;

    private Song song;
    private FloatingActionButton nextFloatingButton;
    private FloatingActionButton previousFloatingButton;

    private RecyclerView recyclerView;

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
        song = songDao.findContentsByTitle(title);
        //setYouTubeView(view);
        setBackImageView(view);
        setTitleTextView(view);
        setOptionsImageView(view);
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
        //  preferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    }


    private void setRecyclerView(View view, Song song)
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recyclerView.setHasFixedSize(true);
        preferenceSettingService = new UserPreferenceSettingService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        songCarViewAdapter = new SongCardViewAdapter(song, this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recyclerView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(floatingActionMenu.isExpanded()) {
                    floatingActionMenu.collapse();
                    int color = 0x00000000;
                    setRecycleViewForegroundColor(color);
                }
                return false;
            }
        });
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

    private void setOptionsImageView(View view)
    {
        ImageView optionMenu = (ImageView) view.findViewById(R.id.optionMenu);
        optionMenu.setOnClickListener(new OptionsImageClickListener());
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

    private void setFloatingActionMenu(final View view, Song song)
    {
        floatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_action_menu);
        if (isPlayVideo(song.getUrlKey())) {
            floatingActionMenu.setVisibility(View.VISIBLE);
            floatingActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener()
            {
                @Override
                public void onMenuExpanded()
                {
                    int color = R.color.gray_transparent;
                    setRecycleViewForegroundColor(ContextCompat.getColor(getActivity(), color));
                }

                @Override
                public void onMenuCollapsed()
                {
                    int color = 0x00000000;
                    setRecycleViewForegroundColor(color);
                }
            });
            setPlaySongFloatingMenuButton(view, song.getUrlKey());
            setPresentSongFloatingMenuButton(view);
        } else {
            floatingActionMenu.setVisibility(View.GONE);
            setPresentSongFloatingButton(view);
        }

    }

    private void setRecycleViewForegroundColor(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            recyclerView.setForeground(new ColorDrawable(color));
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
        final FloatingActionButton presentSongFloatingMenuButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_menu_button);
        presentSongFloatingMenuButton.setVisibility(View.VISIBLE);
        presentSongFloatingMenuButton.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view)
            {
                startPresentActivity();
                if (floatingActionMenu.isExpanded()) {
                    floatingActionMenu.collapse();
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
                startPresentActivity();
            }
        });
    }

    private void startPresentActivity()
    {
        //if (selectedDisplay != null) {
            Intent intent = new Intent(getActivity(), PresentSongActivity.class);
            String title = tilteList.get(Setting.getInstance().getPosition());
            intent.putExtra(CommonConstants.TITLE_KEY, title);
            getActivity().startActivity(intent);
//        } else {
//            Toast.makeText(getActivity(), "Your device is not connected to remote display", Toast.LENGTH_SHORT).show();
//        }
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
            int position = tilteList.indexOf(title);
            Setting.getInstance().setPosition(position);
            return true;
        }
    }

    private class OptionsImageClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            songListAdapterService = new SongListAdapterService();
            songListAdapterService.showPopupmenu(view, title, getFragmentManager(), false);
        }
    }

}