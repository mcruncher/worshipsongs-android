package org.worshipsongs.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.domain.Type;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.PopupMenuService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.ImageUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongsFragment extends Fragment implements TitleAdapter.TitleAdapterListener<Song>
{

    private static final String STATE_KEY = "listViewState";
    private Parcelable state;
    private SearchView searchView;
    private MenuItem filterMenuItem;
    private ListView songListView;

    private List<Song> songs;
    private TitleAdapter<Song> titleAdapter;
    private SongContentViewListener songContentViewListener;
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private PopupMenuService popupMenuService = new PopupMenuService();
    private SongService songService;

    public static SongsFragment newInstance(Bundle bundle)
    {
        SongsFragment songsFragment = new SongsFragment();
        songsFragment.setArguments(bundle);
        return songsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(STATE_KEY);
        }
        songService = new SongService(getActivity());
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        songService.open();
        loadSongs();
        if (!sharedPreferences.contains(CommonConstants.SEARCH_BY_TITLE_KEY)) {
            sharedPreferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply();
        }
    }

    private void loadSongs()
    {
        String type = getType();
        int id = getObjectId();
        if (Type.AUTHOR.name().equalsIgnoreCase(type)) {
            songs = songService.findByAuthorId(id);
        } else if (Type.TOPICS.name().equalsIgnoreCase(type)) {
            songs = songService.findByTopicId(id);
        } else if (Type.SONG_BOOK.name().equalsIgnoreCase(type)) {
            songs = songService.findBySongBookId(id);
        } else {
            songs = songService.findAll();
        }
    }

    private String getType()
    {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getString(CommonConstants.TYPE, Type.SONG.name());
        } else {
            return Type.SONG.name();
        }
    }

    private int getObjectId()
    {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getInt(CommonConstants.ID);
        } else {
            return 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.songs_layout, container, false);
        setListView(view);
        return view;
    }


    private void setListView(View view)
    {
        songListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<Song>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(songService.filterSongs(getType(), "", songs));
        songListView.setAdapter(titleAdapter);
        songListView.setOnItemClickListener(onItemClickListener());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnCloseListener(getSearchViewCloseListener());
        searchView.setOnSearchClickListener(getSearchViewClickListener());
        searchView.setOnQueryTextListener(getQueryTextListener());

        boolean searchByText = sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true);
        searchView.setQueryHint(searchByText ? getSearchByTitleOrNumberPlaceholder(getType()) : getString(R.string.hint_content));
        filterMenuItem = menu.getItem(0).setVisible(false);
        filterMenuItem.setIcon(ImageUtils.resizeBitmapImageFn(getResources(), BitmapFactory.decodeResource(getResources(), getResourceId(searchByText)), 35));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @NonNull
    private SearchView.OnCloseListener getSearchViewCloseListener()
    {
        return new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                filterMenuItem.setVisible(false);
                return false;
            }
        };
    }

    @NonNull
    private View.OnClickListener getSearchViewClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                filterMenuItem.setVisible(true);
            }
        };
    }

    @NonNull
    private SearchView.OnQueryTextListener getQueryTextListener()
    {
        return new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                titleAdapter.addObjects(songService.filterSongs(getType(), query, songs));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                titleAdapter.addObjects(songService.filterSongs(getType(), newText, songs));
                return true;
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.MyDialogTheme));
                builder.setTitle(getString(R.string.search_title));
                builder.setCancelable(true);
                String title = Type.SONG_BOOK.name().equalsIgnoreCase(getType()) ? getString(R.string.search_title_or_content) : getString(R.string.search_type_title);
                builder.setItems(new String[]{title, getString(R.string.search_type_content)}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0) {
                            searchView.setQueryHint(getSearchByTitleOrNumberPlaceholder(getType()));
                            sharedPreferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply();
                            item.setIcon(ImageUtils.resizeBitmapImageFn(getResources(), BitmapFactory.decodeResource(getResources(), getResourceId(true)), 35));
                        } else {
                            searchView.setQueryHint(getString(R.string.hint_content));
                            sharedPreferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, false).apply();
                            item.setIcon(ImageUtils.resizeBitmapImageFn(getResources(), BitmapFactory.decodeResource(getResources(), getResourceId(false)), 35));
                        }
                        searchView.setQuery(searchView.getQuery(), true);
                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getSearchByTitleOrNumberPlaceholder(String type)
    {
        if (type.equalsIgnoreCase(Type.SONG_BOOK.name())) {
            return getString(R.string.hint_title_or_number);
        } else {
            return getString(R.string.hint_title);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (sharedPreferences.getBoolean(CommonConstants.UPDATED_SONGS_KEY, false)) {
            songService.open();
            songs = songService.findAll();
            titleAdapter.clear();
            titleAdapter.addObjects(songService.filterSongs(getType(), "", songs));
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATED_SONGS_KEY, false).apply();
        } else if (state != null) {
            songListView.onRestoreInstanceState(state);
        } else {
            titleAdapter.addObjects(songService.filterSongs(getType(), "", songs));
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() != null) {
                CommonUtils.hideKeyboard(getActivity());
            }
            if (searchView != null) {
                boolean searchByText = sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true);
                searchView.setQueryHint(searchByText ? getSearchByTitleOrNumberPlaceholder(getType()) : getString(R.string.hint_content));
            }
            if (filterMenuItem != null) {
                filterMenuItem.setVisible(false);
            }
        }
    }

    int getResourceId(boolean searchByText)
    {
        return searchByText ? R.drawable.ic_format_title : R.drawable.ic_content_paste;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (this.isAdded()) {
            outState.putParcelable(STATE_KEY, songListView.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause()
    {
        state = songListView.onSaveInstanceState();
        super.onPause();
    }

    public void setSongContentViewListener(SongContentViewListener songContentViewListener)
    {
        this.songContentViewListener = songContentViewListener;
    }

    @Override
    public void setTitleTextView(TextView titleTextView, TextView subTitleTextView, Song song)
    {
        titleTextView.setText(getTitle(song));
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(song.getTitle())) {
            titleTextView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        } else {
            titleTextView.setTextColor(getResources().getColor(R.color.text_black_color));
        }
        subTitleTextView.setVisibility(song.getSongBookNumber() > 0 ? View.VISIBLE : View.GONE);
        subTitleTextView.setText(getString(R.string.song_book_no) + " " + song.getSongBookNumber());
    }

    @NonNull
    private AdapterView.OnItemClickListener onItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Song song = titleAdapter.getItem(position);
                Setting.getInstance().setPosition(0);
                ArrayList<String> titleList = new ArrayList<String>();
                titleList.add(song.getTitle());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titleList);
                if (songContentViewListener == null) {
                    Intent intent = new Intent(getContext(), SongContentViewActivity.class);
                    intent.putExtra(CommonConstants.SONG_BOOK_NUMBER_KEY, song.getSongBookNumber());
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                } else {
                    songContentViewListener.displayContent(song.getTitle(), titleList, 0);
                }
            }
        };
    }

    private String getTitle(Song song)
    {
        try {
            return (preferenceSettingService.isTamil() && song.getTamilTitle().length() > 0) ?
                    song.getTamilTitle() : song.getTitle();
        } catch (Exception e) {
            return song.getTitle();
        }
    }

    @Override
    public void setPlayImageView(ImageView imageView, Song song, int position)
    {
        imageView.setVisibility(isShowPlayIcon(song) ? View.VISIBLE : View.GONE);
        imageView.setOnClickListener(imageOnClickListener(song.getTitle()));
    }

    boolean isShowPlayIcon(Song song)
    {
        String urlKey = song.getUrlKey();
        return urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo();
    }

    @Override
    public void setOptionsImageView(ImageView imageView, Song song, int position)
    {
        imageView.setOnClickListener(imageOnClickListener(song.getTitle()));
    }

    private View.OnClickListener imageOnClickListener(final String title)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                popupMenuService.showPopupmenu((AppCompatActivity) getActivity(), view, title, true);
            }
        };
    }
}
