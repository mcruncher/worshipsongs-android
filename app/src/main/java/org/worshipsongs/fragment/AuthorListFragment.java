package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.service.AuthorListAdapterService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class AuthorListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener
{

    private AuthorDao authorDao;
    private AuthorSongDao authorSongDao;
    private List<Author> authors = new ArrayList<Author>();
    private List<String> authorsNames = new ArrayList<String>();
    private AuthorListAdapterService adapterService = new AuthorListAdapterService();
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }


    private void initSetUp()
    {
        authorDao = new AuthorDao(getActivity());
        authorSongDao = new AuthorSongDao(getActivity());
        loadAuthors();
    }

    private void loadAuthors()
    {
        authorDao.open();
        authorSongDao.open();
        List<AuthorSong> authorSongList = new ArrayList<AuthorSong>();
        authorSongList = authorSongDao.findAuthorsFromAuthorBooks();
        for (AuthorSong authorSong : authorSongList) {
            authors.add(authorDao.findAuthorByID(authorSong.getAuthorId()));
        }
        //authors = authorDao.findAll();
        for (Author author : authors) {
            if (!author.getDisplayName().toLowerCase().contains("unknown") && author.getDisplayName() != null) {
                authorsNames.add(author.getDisplayName());
            }
            Collections.sort(authorsNames);
//            adapter = adapterService.getAuthorListAdapter(authorsNames, getFragmentManager());
//            setListAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate menu to add items to action bar if it is present.

        inflater.inflate(R.menu.action_bar_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager()));
                return true;

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    private List<String> getFilteredAuthors(String text)
    {
        List<String> filteredSongs = new ArrayList<String>();
        for (String author : authorsNames) {
            if (author.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(author);
            }
        }
        return filteredSongs;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setListAdapter(adapterService.getAuthorListAdapter(authorsNames, getFragmentManager()));
            CommonUtils.hideKeyboard(getActivity());
        }
    }

    @Override
    public void onRefresh()
    {

    }
}
