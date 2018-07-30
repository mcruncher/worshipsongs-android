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

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.domain.Type;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.ITabFragment;
import org.worshipsongs.service.SongBookService;

import java.util.List;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class SongBookFragment extends AbstractTabFragment implements TitleAdapter.TitleAdapterListener<SongBook>, ITabFragment
{
    private static final String STATE_KEY = "listViewState";
    private Parcelable state;
    private SongBookService songBookService;
    private List<SongBook> songBookList;
    private ListView songBookListView;
    private TitleAdapter<SongBook> titleAdapter;

    public static SongBookFragment newInstance()
    {
        return new SongBookFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
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
        songBookService = new SongBookService(getActivity());
        songBookList = songBookService.findAll();
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
        songBookListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<SongBook>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(songBookService.filteredSongBooks("", songBookList));
        songBookListView.setAdapter(titleAdapter);
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
                titleAdapter.addObjects(songBookService.filteredSongBooks(query, songBookList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                titleAdapter.addObjects(songBookService.filteredSongBooks(newText, songBookList));
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
            outState.putParcelable(STATE_KEY, songBookListView.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (state != null) {
            songBookListView.onRestoreInstanceState(state);
        } else {
            titleAdapter.addObjects(songBookService.filteredSongBooks("", songBookList));
        }
    }

    @Override
    public void onPause()
    {
        state = songBookListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void setViews(Map<String, Object> objects, SongBook songBook)
    {
        TextView titleTextView = (TextView) objects.get(CommonConstants.TITLE_KEY);
        titleTextView.setText(songBook.getName());
        titleTextView.setOnClickListener(getOnClickListener(songBook));
        setCountView((TextView) objects.get(CommonConstants.SUBTITLE_KEY),
                String.valueOf(songBook.getNoOfSongs()));
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final SongBook songBook)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), SongListActivity.class);
                intent.putExtra(CommonConstants.TYPE, Type.SONG_BOOK.name());
                intent.putExtra(CommonConstants.TITLE_KEY, songBook.getName());
                intent.putExtra(CommonConstants.ID, songBook.getId());
                startActivity(intent);
            }
        };
    }

    @Override
    public int defaultSortOrder()
    {
        return 3;
    }

    @Override
    public String getTitle()
    {
        return "song_books";
    }

    @Override
    public boolean checked()
    {
        return true;
    }

    @Override
    public void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle)
    {
        // Do nothing
    }


}
