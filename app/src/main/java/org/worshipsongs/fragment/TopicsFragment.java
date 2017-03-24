package org.worshipsongs.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;

import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.Topics;
import org.worshipsongs.service.TopicsService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TopicsFragment extends ListFragment
{
    private TopicsService topicsService;
    private List<Topics> topicsList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        topicsService = new TopicsService(getActivity());
        topicsList = topicsService.findAll();
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
                setListAdapter(new TitleAdapter<Topics>(getActivity(), getFilteredTopics(query), "topics"));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                setListAdapter(new TitleAdapter<Topics>(getActivity(), getFilteredTopics(newText), "topics"));
                return true;
            }
        });
        menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private List<Topics> getFilteredTopics(String text)
    {
        List<Topics> filteredTextList = new ArrayList<Topics>();
        for (Topics topics : topicsList) {
            if (topics.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredTextList.add(topics);
            }
        }
        return filteredTextList;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.i(this.getClass().getSimpleName(), "Topic list" + topicsList);
            setListAdapter(new TitleAdapter<Topics>(getActivity(), topicsList, "topics"));
            CommonUtils.hideKeyboard(getActivity());
        }
    }
}
