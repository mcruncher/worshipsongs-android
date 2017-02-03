package org.worshipsongs.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.PresentSongCardViewAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.RemoteSongPresentation;
import org.worshipsongs.domain.Song;
import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : 2.x
 */

public class PresentSongActivity extends AppCompatActivity
{
    private SongDao songDao;
    private PresentSongCardViewAdapter songCarViewAdapter;
    private RemoteSongPresentation defaultRemotePresentation;
    private PresentSongActivity.SongMediaRouterCallBack songMediaRouterCallBack = new PresentSongActivity.SongMediaRouterCallBack();
    private MediaRouter mediaRouter;
    private Song song;
    private FloatingActionButton nextButton;
    private int currentPosition;
    private FloatingActionButton previousButton;
    private ListView listView;
    private PresentSongCardViewAdapter presentSongCardViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.present_song_layout);
        songDao = new SongDao(this);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(CommonConstants.TITLE_KEY);
        song = songDao.findContentsByTitle(title);
        setListView(song);
        // setRecyclerView(song);
        setNextButton(song);
        setPreviousButton(song);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
    }

    private void setListView(Song song)
    {
        listView = (ListView) findViewById(R.id.content_list);
        presentSongCardViewAdapter = new PresentSongCardViewAdapter(PresentSongActivity.this, song.getContents());
        presentSongCardViewAdapter.setItemSelected(0);
        listView.setAdapter(presentSongCardViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                currentPosition = position;
                showNextVerse(position);
                presentSongCardViewAdapter.setItemSelected(currentPosition);
                presentSongCardViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setNextButton(final Song song)
    {
        nextButton = (FloatingActionButton) findViewById(R.id.next_verse_floating_button);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currentPosition = currentPosition + 1;

                if (song.getContents().size() == currentPosition) {
                    nextButton.setVisibility(View.GONE);
                }
                if (song.getContents().size() > currentPosition) {
                    showNextVerse(currentPosition);
                    listView.smoothScrollToPositionFromTop(currentPosition, 2);
                    previousButton.setVisibility(View.VISIBLE);
                    presentSongCardViewAdapter.setItemSelected(currentPosition);
                    presentSongCardViewAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void setPreviousButton(final Song song)
    {
        previousButton = (FloatingActionButton) findViewById(R.id.previous_verse_floating_button);
        previousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentPosition = currentPosition - 1;
                if (currentPosition == song.getContents().size()) {
                    currentPosition = currentPosition - 1;
                }
                if (currentPosition <= song.getContents().size() && currentPosition >= 0) {
                    Log.i(PresentSongActivity.class.getSimpleName(), "Current position after dec: " + currentPosition);
                    showNextVerse(currentPosition);
                    listView.smoothScrollToPosition(currentPosition, 2);
                    nextButton.setVisibility(View.VISIBLE);
                    presentSongCardViewAdapter.setItemSelected(currentPosition);
                    presentSongCardViewAdapter.notifyDataSetChanged();
                }
                if (currentPosition == 0) {
                    previousButton.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (isJellyBean()) {
            mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, songMediaRouterCallBack);
            updatePresentation();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (isJellyBean()) {
            mediaRouter.removeCallback(songMediaRouterCallBack);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        // BEGIN_INCLUDE(onStop)
        // Dismiss the presentation when the activity is not visible.
        if (defaultRemotePresentation != null) {
            defaultRemotePresentation.dismiss();
            defaultRemotePresentation = null;
        }
        // BEGIN_INCLUDE(onStop)
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }


    private final DialogInterface.OnDismissListener remoteDisplayDismissListener =
            new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    if (dialog == defaultRemotePresentation) {
                        defaultRemotePresentation = null;
                    }

                }
            };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updatePresentation()
    {
        Display selectedDisplay = getSelectedDisplay();
        if (defaultRemotePresentation != null && defaultRemotePresentation.getDisplay() != selectedDisplay) {
            defaultRemotePresentation.dismiss();
            defaultRemotePresentation = null;
        }

        if (defaultRemotePresentation == null && selectedDisplay != null) {
            // Initialise a new Presentation for the Display
            defaultRemotePresentation = new RemoteSongPresentation(PresentSongActivity.this, selectedDisplay, song, 0);
            defaultRemotePresentation.setOnDismissListener(remoteDisplayDismissListener);
            try {
                defaultRemotePresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                defaultRemotePresentation = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Display getSelectedDisplay()
    {
        MediaRouter.RouteInfo selectedRoute = mediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display selectedDisplay = null;
        if (selectedRoute != null) {
            selectedDisplay = selectedRoute.getPresentationDisplay();
        }
        return selectedDisplay;
    }

    private boolean isJellyBean()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    class SongMediaRouterCallBack extends MediaRouter.SimpleCallback
    {

        @Override
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info)
        {
            updatePresentation();

        }

        @Override
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info)
        {
            updatePresentation();

        }

        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info)
        {
            updatePresentation();
        }

    }

    private void showNextVerse(int position)
    {
        if (defaultRemotePresentation != null) {
            // a second screen is active and initialized, show the next color
            defaultRemotePresentation.setContent(position);
            defaultRemotePresentation.setSlidePosition(position);
        }
    }

    public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager
    {

        public LinearLayoutManagerWithSmoothScroller(Context context)
        {
            super(context, VERTICAL, false);
        }

        public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position)
        {
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller
        {
            public TopSnappedSmoothScroller(Context context)
            {
                super(context);

            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition)
            {
                return LinearLayoutManagerWithSmoothScroller.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference()
            {
                return SNAP_TO_START;
            }
        }
    }


}
