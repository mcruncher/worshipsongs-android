package org.worshipsongs.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import org.worshipsongs.domain.Type;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.ITabFragment;

import org.worshipsongs.service.DatabaseService;
import org.worshipsongs.service.PopupMenuService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongsFragment extends Fragment implements TitleAdapter.TitleAdapterListener<Song>, ITabFragment
{

    private static final String CLASS_NAME = SongsFragment.class.getSimpleName();
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
    private DatabaseService databaseService;

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
        databaseService = new DatabaseService(getActivity());
        songService = new SongService(getActivity());
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        databaseService.open();
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
        // titleAdapter.addObjects(songService.filterSongs(getType(), "", songs));
        // new LoadSongAsyncTask().execute("");
        updateObjects("");
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
                // titleAdapter.addObjects(songService.filterSongs(getType(), query, songs));
                // new LoadSongAsyncTask().execute(query);
                updateObjects(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                // titleAdapter.addObjects(songService.filterSongs(getType(), query, songs));
                // new LoadSongAsyncTask().execute(query);
                updateObjects(query);
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
            // databaseService.open();
            //  songs = songService.findAll();
            //titleAdapter.clear();
           // titleAdapter.addObjects(songService.filterSongs(getType(), "", songs));
            // new LoadSongAsyncTask().execute("");
            updateObjects("");
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATED_SONGS_KEY, false).apply();
        } else if (state != null) {
            songListView.onRestoreInstanceState(state);
        } else {
            // new LoadSongAsyncTask().execute("");
            updateObjects("");
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
                //new LoadSongAsyncTask().execute("");
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

    @Override
    public void setViews(Map<String, Object> objects, Song song)
    {
        TextView titleTextView = (TextView) objects.get(CommonConstants.TITLE_KEY);
        titleTextView.setText(getTitle(song));
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(song.getTitle())) {
            titleTextView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        } else {
            titleTextView.setTextColor(getResources().getColor(R.color.text_black_color));
        }
        TextView subTitleTextView = (TextView) objects.get(CommonConstants.SUBTITLE_KEY);
        subTitleTextView.setVisibility(song.getSongBookNumber() > 0 ? View.VISIBLE : View.GONE);
        subTitleTextView.setText(getString(R.string.song_book_no) + " " + song.getSongBookNumber());

        ImageView playImageView = (ImageView) objects.get(CommonConstants.PLAY_IMAGE_KEy);
        playImageView.setVisibility(isShowPlayIcon(song) ? View.VISIBLE : View.GONE);
        playImageView.setOnClickListener(imageOnClickListener(song.getTitle()));

        ImageView optionsImageView = (ImageView) objects.get(CommonConstants.OPTIONS_IMAGE_KEY);
        optionsImageView.setVisibility(View.VISIBLE);
        optionsImageView.setOnClickListener(imageOnClickListener(song.getTitle()));
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

    boolean isShowPlayIcon(Song song)
    {
        String urlKey = song.getUrlKey();
        return urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo();
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

    @Override
    public int defaultSortOrder()
    {
        return 0;
    }

    @Override
    public String getTitle()
    {
        return "titles";
    }

    @Override
    public boolean checked()
    {
        return true;
    }

    @Override
    public void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle)
    {
        this.songContentViewListener = songContentViewListener;
    }

    private void updateObjects(final String query)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (titleAdapter != null) {
                    titleAdapter.addObjects(songService.filterSongs(getType(), query, songs));
                }
            }
        });
    }

//    private class LoadSongAsyncTask extends AsyncTask<String, String, Boolean>
//    {
//
//        private ProgressDialog progressDialog;
//
//        LoadSongAsyncTask()
//        {
//            progressDialog = new ProgressDialog(getActivity());
//        }
//
//        @Override
//        protected void onPreExecute()
//        {
//            Log.i(CLASS_NAME, "Preparing to load");
//            progressDialog.setMessage("Loading songs");
//            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params)
//        {
//            final String query = params[0];
//            getActivity().runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    if (titleAdapter != null) {
//                        titleAdapter.addObjects(songService.filterSongs(getType(), query, songs));
//                        Log.i(SongsFragment.class.getSimpleName(), "Finished adding title adapter");
//                    }
//                }
//            });
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean)
//        {
//            Log.i(CLASS_NAME, "Finished loading!");
//            progressDialog.dismiss();
//        }
//    }

}
