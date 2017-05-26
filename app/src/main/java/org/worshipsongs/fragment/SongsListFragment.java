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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.ImageUtils;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class SongsListFragment extends ListFragment
{

    private SongService songService;
    private SongDao songDao;
    private List<Song> songs;
    private ArrayAdapter<Song> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    private SearchView searchView;
    private MenuItem filterMenuItem;

    public static SongsListFragment newInstance(String type, int id)
    {
        SongsListFragment songsListFragment = new SongsListFragment();
        if (StringUtils.isNotBlank(type) && id > 0) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TYPE, type);
            bundle.putInt(CommonConstants.ID, id);
            songsListFragment.setArguments(bundle);
        }
        return songsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "Preparing to load db..");
        songDao = new SongDao(getActivity());
        songService = new SongService(getActivity());
        setHasOptionsMenu(true);
        initSetUp();
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            String type = bundle.getString(CommonConstants.TYPE);
            int id = bundle.getInt(CommonConstants.ID);
            if (type.equalsIgnoreCase("author")) {
                songs = songService.findByAuthorId(id);
            } else if (type.equalsIgnoreCase("topics")) {
                songs = songService.findByTopicId(id);
            }
        } else {
            songs = songDao.findAll();
        }
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
                adapter = adapterService.getSongListAdapter(songService.filterSongs("", songs), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(songService.filterSongs(query, songs), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter = adapterService.getSongListAdapter(songService.filterSongs(newText, songs), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(songService.filterSongs(newText, songs), getFragmentManager()));
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

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListAdapter(adapterService.getSongListAdapter(songService.filterSongs("", songs), getFragmentManager()));
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
                searchView.setQueryHint(searchByText ? getString(R.string.hint_title) : getString(R.string.hint_content));
            }
            if (filterMenuItem != null) {
                filterMenuItem.setVisible(false);
            }
            if (songService != null && adapterService != null) {
                setListAdapter(adapterService.getSongListAdapter(songService.filterSongs("", songs), getFragmentManager()));
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
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}