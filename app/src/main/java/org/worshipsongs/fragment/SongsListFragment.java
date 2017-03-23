package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.ImageUtils;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class SongsListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener
{
    public PopupWindow popupWindow;
    private SongDao songDao;
    private List<Song> songs;
    private ArrayAdapter<Song> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    private SearchView searchView;
    private MenuItem filterMenuItem;


    public SongsListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "Preparing to load db..");
        songDao = new SongDao(getActivity());
        setHasOptionsMenu(true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        initSetUp();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setClickable(false);
    }

    private void initSetUp()
    {
        songDao.open();
        loadSongs();
        if (!sharedPreferences.contains(CommonConstants.SEARCH_BY_TITLE_KEY)) {
            sharedPreferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply();
        }
    }

    private void loadSongs()
    {
        songs = songDao.findAll();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnCloseListener(getSearchViewCloseListener());
        searchView.setOnSearchClickListener(getSearchViewClickListener());
        searchView.setOnQueryTextListener(getQueryTextListener());
        
        boolean searchByText = sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true);
        searchView.setQueryHint(searchByText ? getString(R.string.hint_title) : getString(R.string.hint_content));
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
                adapter = adapterService.getSongListAdapter(getFilteredSong(query, songs), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(query, songs), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter = adapterService.getSongListAdapter(getFilteredSong(newText, songs), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(newText, songs), getFragmentManager()));
                return true;
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.MyDialogTheme));
                builder.setTitle(getString(R.string.search_title));
                builder.setCancelable(true);
                builder.setItems(R.array.searchTypes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0) {
                            searchView.setQueryHint(getString(R.string.hint_title));
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

    List<Song> getFilteredSong(String text, List<Song> songs)
    {
        Set<Song> filteredSongSet = new HashSet<>();
        for (Song song : songs) {
            if (sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true)) {
                if (getTitles(song.getSearchTitle()).toString().toLowerCase().contains(text.toLowerCase())) {
                    filteredSongSet.add(song);
                }
            } else {
                if (song.getSearchLyrics().toLowerCase().contains(text.toLowerCase())) {
                    filteredSongSet.add(song);
                }
            }
        }
        List<Song> filteredSongs = new ArrayList<>(filteredSongSet);
        Collections.sort(filteredSongs, new SongComparator());
        return filteredSongs;
    }

    List<String> getTitles(String searchTitle)
    {
        List<String> titles = new ArrayList<>();
        String[] titleArray = searchTitle.split("@");

        for (String title : titleArray) {
            titles.add(title);
        }
        return titles;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onRefresh()
    {
        Log.d("On refresh in Song list", "");
        setListAdapter(adapterService.getSongListAdapter(songs, getFragmentManager()));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        boolean searchByText = sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true);
        if (isVisibleToUser) {
            CommonUtils.hideKeyboard(getActivity());
            if (searchView != null) {
                searchView.setQueryHint(searchByText ? getString(R.string.hint_title) : getString(R.string.hint_content));
            }
            if (filterMenuItem != null) {
                filterMenuItem.setVisible(false);
            }
            setListAdapter(adapterService.getSongListAdapter(songs, getFragmentManager()));
        }
    }

    int getResourceId(boolean searchByText)
    {
        return searchByText ? R.drawable.ic_format_title : R.drawable.ic_content_paste;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private class SongComparator implements Comparator<Song>
    {

        @Override
        public int compare(Song song1, Song song2)
        {
            return song1.getTitle().compareTo(song2.getTitle());
        }
    }
}