package org.worshipsongs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.adapter.PresentSongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.PresentationScreenService;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class PresentSongActivity extends AppCompatActivity
{
    private SongDao songDao;
    private Song song;
    private FloatingActionButton nextButton;
    private int currentPosition;
    private FloatingActionButton previousButton;
    private ListView listView;
    private PresentSongCardViewAdapter presentSongCardViewAdapter;
    private PresentationScreenService presentationScreenService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.present_song_layout);
        initSetUp();
        presentationScreenService = new PresentationScreenService(PresentSongActivity.this);
        presentationScreenService.showNextVerse(song, 0);
        setListView(song);
        setNextButton(song);
        setPreviousButton(song);
    }

    private void initSetUp()
    {
        songDao = new SongDao(this);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(CommonConstants.TITLE_KEY);
        song = songDao.findContentsByTitle(title);
        setActionBar();
    }

    private void setActionBar()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(song.getTitle());
    }

    private void setListView(final Song song)
    {
        listView = (ListView) findViewById(R.id.content_list);
        presentSongCardViewAdapter = new PresentSongCardViewAdapter(PresentSongActivity.this, song.getContents());
        presentSongCardViewAdapter.setItemSelected(0);
        listView.setAdapter(presentSongCardViewAdapter);
        listView.setOnItemClickListener(new ListViewOnItemClickListener());
    }

    private void setNextButton(final Song song)
    {
        nextButton = (FloatingActionButton) findViewById(R.id.next_verse_floating_button);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new NextButtonOnClickListener(song));
    }

    private void setPreviousButton(final Song song)
    {
        previousButton = (FloatingActionButton) findViewById(R.id.previous_verse_floating_button);
        previousButton.setOnClickListener(new PreviousButtonOnClickListener(song));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class ListViewOnItemClickListener implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            currentPosition = position;
            presentationScreenService.showNextVerse(song, position);
            presentSongCardViewAdapter.setItemSelected(currentPosition);
            presentSongCardViewAdapter.notifyDataSetChanged();
            if (position == 0) {
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.VISIBLE);
            } else if (song.getContents().size() == (position + 1)) {
                nextButton.setVisibility(View.GONE);
                previousButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.VISIBLE);
                previousButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private class NextButtonOnClickListener implements View.OnClickListener
    {
        private Song song;

        NextButtonOnClickListener(Song song)
        {
            this.song = song;
        }

        @Override
        public void onClick(View v)
        {
            currentPosition = currentPosition + 1;
            if (song.getContents().size() == currentPosition) {
                nextButton.setVisibility(View.GONE);
            }
            if (song.getContents().size() > currentPosition) {
                presentationScreenService.showNextVerse(song, currentPosition);
                listView.smoothScrollToPositionFromTop(currentPosition, 2);
                previousButton.setVisibility(View.VISIBLE);
                presentSongCardViewAdapter.setItemSelected(currentPosition);
                presentSongCardViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private class PreviousButtonOnClickListener implements View.OnClickListener
    {
        private Song song;

        PreviousButtonOnClickListener(Song song)
        {
            this.song = song;
        }

        @Override
        public void onClick(View v)
        {
            currentPosition = currentPosition - 1;
            if (currentPosition == song.getContents().size()) {
                currentPosition = currentPosition - 1;
            }
            if (currentPosition <= song.getContents().size() && currentPosition >= 0) {
                presentationScreenService.showNextVerse(song, currentPosition);
                listView.smoothScrollToPosition(currentPosition, 2);
                nextButton.setVisibility(View.VISIBLE);
                presentSongCardViewAdapter.setItemSelected(currentPosition);
                presentSongCardViewAdapter.notifyDataSetChanged();
            }
            if (currentPosition == 0) {
                previousButton.setVisibility(View.GONE);
            }
        }
    }


}
