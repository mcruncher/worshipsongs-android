package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;


import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class SongsListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener
{

    public PopupWindow popupWindow;
    private SongDao songDao;
    private List<Song> songs;
    private List<String> songsTitleList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    private SearchView searchView;
    private WorshipSongApplication application = new WorshipSongApplication();
    private String selectedSong;
    private UtilitiesService utilitiesService = new UtilitiesService();
    private List<Verse> verseList;
    private String[] serviceNames;
    private CommonService commonService = new CommonService();
    private ListDialogFragment dialogFragment;
    private MenuItem mSearchMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        songDao = new SongDao(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        initSetUp();
    }

    private void initSetUp()
    {
        songDao.open();
        loadSongs();
    }

    private void loadSongs()
    {
        songs = songDao.findTitles();
        for (Song song : songs) {
            if (!song.getTitle().equals(null)) {
                songsTitleList.add(song.getTitle());
            }
        }
        List<String> serviceNames = new ArrayList<String>();
        serviceNames.addAll(commonService.readServiceName());
        setServiceNames(serviceNames);
        adapter = adapterService.getSongListAdapter(songsTitleList, getFragmentManager());
        setListAdapter(adapter);
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter = adapterService.getSongListAdapter(getFilteredSong(query), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(query), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter = adapterService.getSongListAdapter(getFilteredSong(newText), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(newText), getFragmentManager()));
                return true;

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredSong(String text)
    {
        List<String> filteredSongs = new ArrayList<String>();
        for (String song : songsTitleList) {
            if (song.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setQuery("", false);
//        searchView.clearFocus();
        super.onPrepareOptionsMenu(menu);
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
        List<String> serviceNames = new ArrayList<String>();
        serviceNames.addAll(commonService.readServiceName());
        //setServiceNames(serviceNames);
        setListAdapter(adapterService.getSongListAdapter(songsTitleList, getFragmentManager()));
    }

    public String[] getServiceNames()
    {
        return serviceNames;
    }

    public void setServiceNames(List<String> names)
    {
        names.add(0, "New playlist...");
        serviceNames = new String[names.size()];
        names.toArray(serviceNames);

    }

}