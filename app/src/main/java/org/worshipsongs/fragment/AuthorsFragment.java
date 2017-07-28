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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.Type;
import org.worshipsongs.service.AuthorService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : Seenivasan,Madasamy
 * @Version : 1.0
 */
public class AuthorsFragment extends Fragment implements TitleAdapter.TitleAdapterListener<Author>
{
    private static final String STATE_KEY = "listViewState";
    private Parcelable state;
    private AuthorService authorService;
    private List<Author> authorList = new ArrayList<>();
    private ListView authorListView;
    private TitleAdapter<Author> titleAdapter;

    public static AuthorsFragment newInstance()
    {
        return new AuthorsFragment();
    }

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
        authorService = new AuthorService(getContext());
        for (Author author : authorService.findAll()) {
            if (!author.getName().toLowerCase().contains("unknown") && author.getName() != null) {
                authorList.add(author);
            }
        }
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
        authorListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<Author>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(getFilteredAuthors(""));
        authorListView.setAdapter(titleAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                titleAdapter.addObjects(getFilteredAuthors(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                titleAdapter.addObjects(getFilteredAuthors(newText));
                return true;

            }
        });
        menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<Author> getFilteredAuthors(String text)
    {
        List<Author> filteredAuthors = new ArrayList<Author>();
        if (StringUtils.isNotBlank(text)) {
            for (Author author : authorList) {
                if (author.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredAuthors.add(author);
                }
            }
        } else {
            filteredAuthors.addAll(authorList);
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
    public void onSaveInstanceState(Bundle outState)
    {
        if (this.isAdded()) {
            outState.putParcelable(STATE_KEY, authorListView.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (state != null) {
            authorListView.onRestoreInstanceState(state);
        } else {
            titleAdapter.addObjects(getFilteredAuthors(""));
        }
    }

    @Override
    public void onPause()
    {
        state = authorListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            CommonUtils.hideKeyboard(getActivity());
        }
    }

    @Override
    public void setTitleTextView(TextView textView, final Author author)
    {
        textView.setText(author.getName());
        textView.setOnClickListener(textViewOnClickListener(author));
    }

    @NonNull
    private View.OnClickListener textViewOnClickListener(final Author author)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), SongListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CommonConstants.TYPE, Type.AUTHOR.name());
                intent.putExtra(CommonConstants.TITLE_KEY, author.getName());
                intent.putExtra(CommonConstants.ID, author.getId());
                startActivity(intent);
            }
        };
    }

    @Override
    public void setPlayImageView(ImageView imageView, Author author, int position)
    {
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void setOptionsImageView(ImageView imageView, Author author, int position)
    {
        imageView.setVisibility(View.GONE);
    }
}
