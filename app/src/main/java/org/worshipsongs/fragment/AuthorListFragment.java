package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.Author;
import org.worshipsongs.service.AuthorService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class AuthorListFragment extends ListFragment
{
    private AuthorService authorService;
    private TitleAdapter titleAdapter;
    private List<Author> authorList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(titleAdapter = new TitleAdapter<Author>(getActivity(), getFilteredAuthors(""), "author"));
    }

    private void initSetUp()
    {
        authorService = new AuthorService(getContext());
        for (Author author : authorService.findAll()) {
            if (!author.getName().toLowerCase().contains("unknown") && author.getName() != null) {
                authorList.add(author);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
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
                setListAdapter(titleAdapter = new TitleAdapter<Author>(getActivity(), getFilteredAuthors(query), "author"));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                getListView().invalidateViews();
                setListAdapter(titleAdapter = new TitleAdapter<Author>(getActivity(), getFilteredAuthors(newText), "author"));
                return true;

            }
        });
        menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<Author> getFilteredAuthors(String text)
    {
        List<Author> filteredAuthors = new ArrayList<Author>();
        for (Author author : authorList) {
            if (author.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredAuthors.add(author);
            }
        }
        return filteredAuthors;
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
            setListAdapter(titleAdapter = new TitleAdapter<Author>(getActivity(), authorList, "author"));
            CommonUtils.hideKeyboard(getActivity());
        }
    }

}
