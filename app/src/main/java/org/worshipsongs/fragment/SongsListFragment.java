package org.worshipsongs.fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.worship.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongsListFragment extends ListFragment {

    private SongDao songDao;
    private List<Song> songs;
    private List<String> songsTitleList = new ArrayList<String>();
    private CommonService commonService = new CommonService();
    private ArrayAdapter<String> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        songDao = new SongDao(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        initSetUp();
    }

    private void initSetUp() {
        songDao.open();
        loadSongs();
    }

    private void loadSongs() {
        songs = songDao.findTitles();
        for (Song song : songs) {
            if (!song.getTitle().equals(null)) {
                songsTitleList.add(song.getTitle());
            }
        }
        adapter = adapterService.getSongListAdapter(songsTitleList, getFragmentManager());
        //adapterService.setServiceNames(commonService.readServiceName());
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = new MenuInflater(getActivity().getApplicationContext());
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = adapterService.getSongListAdapter(getFilteredSong(newText), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(newText), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getSongListAdapter(getFilteredSong(query), getFragmentManager());
                setListAdapter(adapterService.getSongListAdapter(getFilteredSong(query), getFragmentManager()));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredSong(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String song : songsTitleList) {
            if (song.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}