package org.worshipsongs.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.youtube.player.YouTubePlayer;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.adapter.PresentSongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.AuthorService;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.IAuthorService;
import org.worshipsongs.service.PopupMenuService;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.PermissionUtils;

import java.util.ArrayList;

/**
 * Author: Madasamy, Vignesh Palanisamy
 * version: 1.0.0
 */

public class SongContentPortraitViewFragment extends Fragment implements ISongContentPortraitViewFragment
{
    public static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";
    private String title = "";
    private ArrayList<String> tilteList = new ArrayList<>();
    private int millis;
    private YouTubePlayer youTubePlayer;
    private UserPreferenceSettingService preferenceSettingService;
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private IAuthorService authorService = new AuthorService(WorshipSongApplication.getContext());
    private PopupMenuService popupMenuService;
    private FloatingActionsMenu floatingActionMenu;
    private Song song;
    private ListView listView;
    private PresentSongCardViewAdapter presentSongCardViewAdapter;
    private FloatingActionButton nextButton;
    private FloatingActionButton previousButton;
    private FloatingActionButton presentSongFloatingButton;
    private PresentationScreenService presentationScreenService;
    private CustomTagColorService customTagColorService = new CustomTagColorService();


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
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(CommonUtils.isPhone(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.song_content_portrait_view, container, false);
        initSetUp();
        setListView(view, song);
        setFloatingActionMenu(view, song);
        setNextButton(view);
        setPreviousButton(view);
        view.setOnTouchListener(new SongContentPortraitViewTouchListener());
        onBecameVisible(song);
        return view;
    }


    private void initSetUp()
    {
        // showStatusBar();
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        song = songDao.findContentsByTitle(title);
        song.setAuthorName(authorService.findNameByTitle(title));
        preferenceSettingService = new UserPreferenceSettingService();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setListView(View view, final Song song)
    {
        listView = (ListView) view.findViewById(R.id.content_list);
        presentSongCardViewAdapter = new PresentSongCardViewAdapter(getActivity(), song.getContents());
        listView.setAdapter(presentSongCardViewAdapter);
        listView.setOnItemClickListener(new ListViewOnItemClickListener());
        listView.setOnItemLongClickListener(new ListViewOnItemLongClickListener());
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
                    presentSelectedVerse(0);
                    floatingActionMenu.setVisibility(View.GONE);
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
                    presentSelectedVerse(0);
                    presentSongFloatingButton.setVisibility(View.GONE);
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

    @Override
    public void fragmentBecameVisible()
    {
        onBecameVisible(song);
    }

    private void onBecameVisible(Song song)
    {
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.equals(song) && presentationScreenService.getPresentation() != null) {
            setPresentation(song);
        } else {
            hideOrShowComponents(song);
        }
        setActionBarTitle();
    }

    private void setPresentation(Song song)
    {
        int currentPosition = Setting.getInstance().getSlidePosition();
        presentSelectedVerse(currentPosition);
        if (floatingActionMenu != null) {
            floatingActionMenu.setVisibility(View.GONE);
        }
        if (presentSongFloatingButton != null) {
            presentSongFloatingButton.setVisibility(View.GONE);
        }
        nextButton.setVisibility((song.getContents().size() - 1) == currentPosition ? View.GONE : View.VISIBLE);
        previousButton.setVisibility(currentPosition == 0 ? View.GONE : View.VISIBLE);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void hideOrShowComponents(Song song)
    {
        if (nextButton != null) {
            nextButton.setVisibility(View.GONE);
        }
        if (previousButton != null) {
            previousButton.setVisibility(View.GONE);
        }
        if (isPlayVideo(song.getUrlKey()) && isPresentSong() && floatingActionMenu != null) {
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

    private class NextButtonOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            int position = presentSongCardViewAdapter.getSelectedItem() + 1;
            listView.smoothScrollToPositionFromTop(position, 2);
            presentSelectedVerse(position <= song.getContents().size() ? position : (position - 1));
        }
    }

    private class PreviousButtonOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            int position = presentSongCardViewAdapter.getSelectedItem() - 1;
            int previousPosition = position >= 0 ? position : 0;
            listView.smoothScrollToPosition(previousPosition, 2);
            presentSelectedVerse(previousPosition);
        }
    }

    private class ListViewOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (previousButton.getVisibility() == View.VISIBLE || nextButton.getVisibility() == View.VISIBLE) {
                listView.smoothScrollToPositionFromTop(position, 2);
                presentSelectedVerse(position);
            }
            if (floatingActionMenu != null && floatingActionMenu.isExpanded()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                floatingActionMenu.collapse();
                int color = 0x00000000;
                setListViewForegroundColor(color);
            }
        }

    }

    private void presentSelectedVerse(int position)
    {
        if (presentationScreenService.getPresentation() != null) {
            getPresentationScreenService().showNextVerse(song, position);
            presentSongCardViewAdapter.setItemSelected(position);
            presentSongCardViewAdapter.notifyDataSetChanged();
            previousButton.setVisibility(position <= 0 ? View.GONE : View.VISIBLE);
            nextButton.setVisibility(position >= song.getContents().size() - 1 ? View.GONE : View.VISIBLE);
        }
    }

    private class ListViewOnItemLongClickListener implements AdapterView.OnItemLongClickListener
    {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (isCopySelectedVerse()) {
                String selectedVerse = song.getContents().get(position);
                presentSongCardViewAdapter.setItemSelected(position);
                presentSongCardViewAdapter.notifyDataSetChanged();
                shareSongInSocialMedia(selectedVerse);
            }
            return false;
        }

        void shareSongInSocialMedia(String selectedText)
        {
            String formattedContent = song.getTitle() + "\n\n" +
                    customTagColorService.getFormattedLines(selectedText) + "\n" + String.format(getString(R.string.verse_share_info), getString(R.string.app_name));
            Intent textShareIntent = new Intent(Intent.ACTION_SEND);
            textShareIntent.putExtra(Intent.EXTRA_TEXT, formattedContent);
            textShareIntent.setType("text/plain");
            Intent intent = Intent.createChooser(textShareIntent, "Share verse with...");
            getActivity().startActivity(intent);
        }

        boolean isCopySelectedVerse()
        {
            return !isPresentSong() || ((isPlayVideo(song.getUrlKey()) && floatingActionMenu != null && floatingActionMenu.getVisibility() == View.VISIBLE) ||
                    (presentSongFloatingButton != null && presentSongFloatingButton.getVisibility() == View.VISIBLE));

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        if (CommonUtils.isPhone(getContext())) {
            inflater.inflate(R.menu.action_bar_options, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        Log.i(SongContentPortraitViewFragment.class.getSimpleName(), "Menu item " + item.getItemId() + " " + R.id.options);
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.options:
                Log.i(SongContentPortraitViewFragment.class.getSimpleName(), "On tapped options");
                popupMenuService = new PopupMenuService();
                PermissionUtils.isStoragePermissionGranted(getActivity());
                popupMenuService.showPopupmenu((AppCompatActivity) getActivity(), getActivity().findViewById(R.id.options), title, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setActionBarTitle();
        }
    }

    private void setActionBarTitle()
    {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        try {
            if (preferenceSettingService != null && tilteList.size() > 0 && CommonUtils.isPhone(getContext())) {
                String title;
                if (tilteList.size() == 1) {
                    title = tilteList.get(0);
                } else {
                    title = tilteList.get(Setting.getInstance().getPosition());
                }
                Song song = songDao.findContentsByTitle(title);
                appCompatActivity.setTitle(getTitle(song, title));
            }
        } catch (Exception ex) {
            appCompatActivity.setTitle(title);
        }
    }

    private String getTitle(Song song, String title)
    {
        try {
            return (preferenceSettingService.isTamil() && song.getTamilTitle().length() > 0) ?
                    song.getTamilTitle() : song.getTitle();
        } catch (Exception e) {
            return title;
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
