package org.worshipsongs.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.Topics;
import org.worshipsongs.domain.Type;
import org.worshipsongs.service.TopicsService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.CommonUtils;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TopicsFragment extends Fragment implements TitleAdapter.TitleAdapterListener<Topics>
{
    private static final String STATE_KEY = "listViewState";
    private Parcelable state;
    private TopicsService topicsService;
    private List<Topics> topicsList;
    private ListView topicsListView;
    private TitleAdapter<Topics> titleAdapter;
    private UserPreferenceSettingService userPreferenceSettingService = new UserPreferenceSettingService();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(STATE_KEY);
        }
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        topicsService = new TopicsService(getActivity());
        topicsList = topicsService.findAll();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.songs_layout, container, false);
        setListView(view);
        return view;
    }

    private void setListView(View view)
    {
        topicsListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<Topics>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(topicsService.filteredTopics("", topicsList));
        topicsListView.setAdapter(titleAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.action_search));
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                titleAdapter.addObjects(topicsService.filteredTopics(query, topicsList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                titleAdapter.addObjects(topicsService.filteredTopics(newText, topicsList));
                return true;
            }
        });
        menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (this.isAdded()) {
            outState.putParcelable(STATE_KEY, topicsListView.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (state != null) {
            topicsListView.onRestoreInstanceState(state);
        } else {
            titleAdapter.addObjects(topicsService.filteredTopics("", topicsList));
        }
    }

    @Override
    public void onPause()
    {
        state = topicsListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && topicsList != null) {
            CommonUtils.hideKeyboard(getActivity());
        }
    }


    @NonNull
    private View.OnClickListener getOnClickListener(final Topics topics)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), SongListActivity.class);
                intent.putExtra(CommonConstants.TYPE, Type.TOPICS.name());
                intent.putExtra(CommonConstants.TITLE_KEY, getTopicsName(topics));
                intent.putExtra(CommonConstants.ID, topics.getId());
                startActivity(intent);
            }
        };
    }

    private String getTopicsName(Topics topics)
    {
        return userPreferenceSettingService.isTamil() ? topics.getTamilName() : topics.getDefaultName();
    }

    @Override
    public void setTitleTextView(TextView textView, TextView subTitleTextView, Topics topics)
    {
        textView.setText(getTopicsName(topics));
        textView.setOnClickListener(getOnClickListener(topics));
    }

    @Override
    public void setPlayImageView(ImageView imageView, Topics topics, int position)
    {
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void setOptionsImageView(ImageView imageView, Topics topics, int position)
    {
        imageView.setVisibility(View.GONE);
    }
}
