package org.worshipsongs.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.youtube.player.YouTubePlayer;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.adapter.PresentSongCardViewAdapter;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;

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
    private UserPreferenceSettingService preferenceSettingService;
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private AuthorSongDao authorSongDao;
    private SongListAdapterService songListAdapterService;
    private FloatingActionsMenu floatingActionMenu;
    private Song song;
    private ListView listView;
    private PresentSongCardViewAdapter presentSongCardViewAdapter;
    private FloatingActionButton nextButton;
    private FloatingActionButton previousButton;
    private int currentPosition;
    private FloatingActionButton presentSongFloatingButton;
    private PresentationScreenService presentationScreenService;


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
        final View view = inflater.inflate(R.layout.song_content_portrait_view, container, false);
        initSetUp();
        setBackImageView(view);
        setTitleTextView(view);
        setOptionsImageView(view);
        setListView(view, song);
        setFloatingActionMenu(view, song);
        setNextButton(view);
        setPreviousButton(view);
        view.setOnTouchListener(new SongContentPortraitViewTouchListener());
        return view;
    }

    private void initSetUp()
    {
        showStatusBar();
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        song = songDao.findContentsByTitle(title);
        authorSongDao = new AuthorSongDao(getContext());
        AuthorSong authorSong = authorSongDao.findByTitle(song.getTitle());
        song.setAuthorName(authorSong.getAuthor().getDisplayName());
        preferenceSettingService = new UserPreferenceSettingService();
    }

    private void showStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
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

    private void setTitleTextView(View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.song_title);
        textView.setText(title);
    }

    private void setOptionsImageView(View view)
    {
        ImageView optionMenu = (ImageView) view.findViewById(R.id.optionMenu);
        optionMenu.setOnClickListener(new OptionsImageClickListener());
    }


    private void setListView(View view, Song song)
    {
        listView = (ListView) view.findViewById(R.id.content_list);
        presentSongCardViewAdapter = new PresentSongCardViewAdapter(getActivity(), song.getContents());
        listView.setAdapter(presentSongCardViewAdapter);
        listView.setOnItemClickListener(new ListViewOnItemClickListener());
    }

    private void setFloatingActionMenu(final View view, Song song)
    {
        floatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_action_menu);
        if (isPlayVideo(song.getUrlKey()) && isPresentSong()) {
            floatingActionMenu.setVisibility(View.VISIBLE);
            floatingActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener()
            {
                @Override
                public void onMenuExpanded()
                {
                    int color = R.color.gray_transparent;
                    setListViewForegroundColor(ContextCompat.getColor(getActivity(), color));
                }

                @Override
                public void onMenuCollapsed()
                {
                    int color = 0x00000000;
                    setListViewForegroundColor(color);
                }
            });
            setPlaySongFloatingMenuButton(view, song.getUrlKey());
            setPresentSongFloatingMenuButton(view);
        } else {
            floatingActionMenu.setVisibility(View.GONE);
            if (isPresentSong()) {
                setPresentSongFloatingButton(view);
            }
            if (isPlayVideo(song.getUrlKey())) {
                setPlaySongFloatingButton(view, song.getUrlKey());
            }
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
                if (floatingActionMenu.isExpanded()) {
                    floatingActionMenu.collapse();
                }
                if (presentationScreenService.getPresentation() != null) {
                    currentPosition = 0;
                    getPresentationScreenService().showNextVerse(song, currentPosition);
                    presentSongCardViewAdapter.setItemSelected(0);
                    presentSongCardViewAdapter.notifyDataSetChanged();
                    floatingActionMenu.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    Toast.makeText(getActivity(), "Your device is not connected to any remote display", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setPresentSongFloatingButton(View view)
    {
        presentSongFloatingButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_button);
        presentSongFloatingButton.setVisibility(View.VISIBLE);
        presentSongFloatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (presentationScreenService.getPresentation() != null) {
                    currentPosition = 0;
                    getPresentationScreenService().showNextVerse(song, currentPosition);
                    presentSongFloatingButton.setVisibility(View.GONE);
                    presentSongCardViewAdapter.setItemSelected(0);
                    presentSongCardViewAdapter.notifyDataSetChanged();
                    nextButton.setVisibility(View.VISIBLE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    Toast.makeText(getActivity(), "Your device is not connected to any remote display", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setPlaySongFloatingButton(View view, final String urlKey)
    {
        FloatingActionButton playSongFloatingButton = (FloatingActionButton) view.findViewById(R.id.play_song_floating_button);
        playSongFloatingButton.setVisibility(View.VISIBLE);
        playSongFloatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showYouTube(urlKey);
            }
        });
    }

    private void showYouTube(String urlKey)
    {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(getActivity(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        youTubeIntent.putExtra("title", title);
        getActivity().startActivity(youTubeIntent);
    }


    private void setNextButton(View view)
    {
        nextButton = (FloatingActionButton) view.findViewById(R.id.next_verse_floating_button);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(new NextButtonOnClickListener());
    }

    private void setPreviousButton(View view)
    {
        previousButton = (FloatingActionButton) view.findViewById(R.id.previous_verse_floating_button);
        previousButton.setVisibility(View.GONE);
        previousButton.setOnClickListener(new PreviousButtonOnClickListener());
    }

    private class NextButtonOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            currentPosition = currentPosition + 1;
            if ((song.getContents().size() - 1) == currentPosition) {
                nextButton.setVisibility(View.GONE);
            }
            if (song.getContents().size() > currentPosition) {
                getPresentationScreenService().showNextVerse(song, currentPosition);
                listView.smoothScrollToPositionFromTop(currentPosition, 2);
                previousButton.setVisibility(View.VISIBLE);
                presentSongCardViewAdapter.setItemSelected(currentPosition);
                presentSongCardViewAdapter.notifyDataSetChanged();

            }
        }
    }

    private class PreviousButtonOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            currentPosition = currentPosition - 1;
            if (currentPosition == song.getContents().size()) {
                currentPosition = currentPosition - 1;
            }
            if (currentPosition <= song.getContents().size() && currentPosition >= 0) {
                getPresentationScreenService().showNextVerse(song, currentPosition);
                listView.smoothScrollToPosition(currentPosition, 2);
                nextButton.setVisibility(View.VISIBLE);
                presentSongCardViewAdapter.setItemSelected(currentPosition);
                presentSongCardViewAdapter.notifyDataSetChanged();
            }
            if (currentPosition == 0) {
                previousButton.setVisibility(View.GONE);
            }
        }
    }

    private class ListViewOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (isPlayVideo(song.getUrlKey())) {
                if (floatingActionMenu != null && floatingActionMenu.getVisibility() == View.GONE && isPresentSong()) {
                    setOnItemClickListener(position);
                }
            } else {
                if (presentSongFloatingButton != null && presentSongFloatingButton.getVisibility() == View.GONE) {
                    setOnItemClickListener(position);
                }
            }
            if (floatingActionMenu != null && floatingActionMenu.isExpanded()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                floatingActionMenu.collapse();
                int color = 0x00000000;
                setListViewForegroundColor(color);
            }
        }

        private void setOnItemClickListener(int position)
        {
            currentPosition = position;
            getPresentationScreenService().showNextVerse(song, position);
            presentSongCardViewAdapter.setItemSelected(currentPosition);
            presentSongCardViewAdapter.notifyDataSetChanged();
            if (presentSongFloatingButton != null) {
                presentSongFloatingButton.setVisibility(View.GONE);
            }

            if (position == 0) {
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.VISIBLE);
            } else if (song.getContents().size() == (position + 1)) {
                nextButton.setVisibility(View.GONE);
                previousButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.VISIBLE);
                previousButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListViewForegroundColor(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listView.setForeground(new ColorDrawable(color));
        }
    }

    private boolean isPlayVideo(String urrlKey)
    {
        boolean playVideoStatus = preferenceSettingService.isPlayVideo();
        return urrlKey != null && urrlKey.length() > 0 && playVideoStatus;
    }

    private boolean isPresentSong()
    {
        return presentationScreenService != null && presentationScreenService.getPresentation() != null;
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (nextButton != null) {
            nextButton.setVisibility(View.GONE);
        }
        if (previousButton != null) {
            previousButton.setVisibility(View.GONE);
        }
        if (song != null && isPlayVideo(song.getUrlKey()) && isPresentSong() && floatingActionMenu != null) {
            floatingActionMenu.setVisibility(View.VISIBLE);
        } else if (presentSongFloatingButton != null) {
            presentSongFloatingButton.setVisibility(View.VISIBLE);
        }
        if (presentSongCardViewAdapter != null) {
            presentSongCardViewAdapter.setItemSelected(-1);
            presentSongCardViewAdapter.notifyDataSetChanged();
        }
        if (listView != null) {
            listView.smoothScrollToPosition(0);
        }
    }

    public PresentationScreenService getPresentationScreenService()
    {
        return presentationScreenService;
    }

    public void setPresentationScreenService(PresentationScreenService presentationScreenService)
    {
        this.presentationScreenService = presentationScreenService;
    }

}