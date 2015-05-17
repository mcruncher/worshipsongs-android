package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.service.AuthorListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Seenivasan on 5/17/2015.
 */
public class NewAuthorListFragment extends ListFragment {

    private AuthorDao authorDao;
    private List<Author> authors = new ArrayList<Author>();
    private List<String> authorsNames = new ArrayList<String>();
    private AuthorListAdapterService adapterService = new AuthorListAdapterService();
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp() {
        authorDao = new AuthorDao(getActivity());
        loadAuthors();
    }

    private void loadAuthors() {
        authorDao.open();
        authors = authorDao.findAll();
        for (Author author : authors) {
            if (!author.getDisplayName().toLowerCase().contains("unknown") && author.getDisplayName() != null) {
                authorsNames.add(author.getDisplayName());
            }
            adapter = adapterService.getAuthorListAdapter(authorsNames, getFragmentManager());
            setListAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = new MenuInflater(getActivity().getApplicationContext());
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager()));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredAuthors(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String author : authorsNames) {
            if (author.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(author);
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
