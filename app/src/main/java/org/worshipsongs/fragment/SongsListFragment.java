package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.utils.CommonUtils;
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
        getListView().setClickable(true);

    }

    private void initSetUp()
    {
        songDao.open();
        loadSongs();
    }

    private void loadSongs()
    {
        songs = songDao.findAll();
        List<String> serviceNames = new ArrayList<String>();
        // serviceNames.addAll(commonService.readServiceName());
        //setServiceNames(serviceNames);
        //adapter = adapterService.getNewSongListAdapter(songs, getFragmentManager());
        // setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.action_bar_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter = adapterService.getNewSongListAdapter(getFilteredSong(query, songs), getFragmentManager());
                setListAdapter(adapterService.getNewSongListAdapter(getFilteredSong(query, songs), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter = adapterService.getNewSongListAdapter(getFilteredSong(newText, songs), getFragmentManager());
                setListAdapter(adapterService.getNewSongListAdapter(getFilteredSong(newText, songs), getFragmentManager()));
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    List<Song> getFilteredSong(String text, List<Song> songs)
    {
        Set<Song> filteredSongSet = new HashSet<>();
        for (Song song : songs) {
            if (getTitles(song.getSearchTitle()).toString().toLowerCase().contains(text.toLowerCase())) {
                filteredSongSet.add(song);
            }
            if (song.getSearchLyrics().toLowerCase().contains(text.toLowerCase())) {
                filteredSongSet.add(song);
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
        // SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.clearFocus();
        super.onPrepareOptionsMenu(menu);
        //menu.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh()
    {
        Log.d("On refresh in Song list", "");
        setListAdapter(adapterService.getNewSongListAdapter(songs, getFragmentManager()));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(this.getClass().getSimpleName(), "Is visible to user ?" + isVisibleToUser);
        if (isVisibleToUser) {
            CommonUtils.hideKeyboard(getActivity());
            setListAdapter(adapterService.getNewSongListAdapter(songs, getFragmentManager()));

//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            getListView().setSelector(android.R.color.darker_gray);

        }
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