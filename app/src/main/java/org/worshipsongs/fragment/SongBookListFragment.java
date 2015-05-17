package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import org.worshipsongs.dao.SongBookDao;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.service.AuthorListAdapterService;
import org.worshipsongs.service.SongBookListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/17/2015.
 */
public class SongBookListFragment extends ListFragment {

    private SongBookDao songBookDao;
    private List<SongBook> songBooks;
    private List<String> songBookNames = new ArrayList<String>();
    private SongBookListAdapterService adapterService = new SongBookListAdapterService();
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp() {
        songBookDao = new SongBookDao(getActivity());
        songBookDao.open();
        loadSongBooks();
    }

    private void loadSongBooks() {
        songBooks = songBookDao.findAll();
        for (SongBook songBook : songBooks) {
            if (!songBook.getName().equals(null)) {
                songBookNames.add(songBook.getName());
            }
        }
        adapter = adapterService.getSongBookListAdapter(songBookNames, getFragmentManager());
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
                adapter = adapterService.getSongBookListAdapter(getFilteredAuthors(newText), getFragmentManager());
                setListAdapter(adapterService.getSongBookListAdapter(getFilteredAuthors(newText), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getSongBookListAdapter(getFilteredAuthors(query), getFragmentManager());
                setListAdapter(adapterService.getSongBookListAdapter(getFilteredAuthors(query), getFragmentManager()));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredAuthors(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String songBookName : songBookNames) {
            if (songBookName.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(songBookName);
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
