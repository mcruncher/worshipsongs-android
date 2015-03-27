package org.worshipsongs.page.component.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorListFragment extends Fragment {
    private ListView songListView;
    private VerseParser verseparser;
    private AuthorDao authorDao;
    private SongDao songDao;
    private AuthorSongDao authorSongDao;
    private List<AuthorSong> authorSongs;
    private List<Author> authors;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<Author> adapter;
    private String[] dataArray;
    private LinearLayout FragmentLayout;
    private android.support.v4.app.FragmentActivity FragmentActivity;
    List<String> songName;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity = (FragmentActivity) super.getActivity();
        FragmentLayout = (LinearLayout) inflater.inflate(R.layout.songs_list_activity, container, false);
        setHasOptionsMenu(true);
        songListView = (ListView) FragmentLayout.findViewById(R.id.song_list_view);
        authorDao = new AuthorDao(getActivity());
        authorSongDao = new AuthorSongDao(getActivity());
        songDao = new SongDao(getActivity());
        verseparser = new VerseParser();
        initSetUp();
        return FragmentLayout;
    }

    private void initSetUp() {
        authorDao.open();
        loadAuthors();
        dataArray = new String[authors.size()];
        for (int i = 0; i < authors.size(); i++) {
            dataArray[i] = authors.get(i).toString();
        }
    }

    private void loadAuthors() {
        authors = authorDao.findAll();
        adapter = new ArrayAdapter<Author>(getActivity(), android.R.layout.simple_list_item_1, authors);
        songListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = new MenuInflater(getActivity().getApplicationContext());
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) this.FragmentActivity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.FragmentActivity.getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);
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
