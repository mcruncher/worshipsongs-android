package org.worshipsongs.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.worship.R;

/***********************************************************************************
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Scott Cooper
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/

/**
 * This Activity shows how the YouTubePlayerView can be used to create a "Lightbox" similar to that of the
 * StandaloneYouTubePlayer. Using this method, we can improve upon it by performing transitions and allowing for
 * custom behaviour, such as closing when the user clicks anywhere outside the player
 * We manage to avoid rebuffering the video by setting some configchange flags on this activities declaration in the manifest.
 */
public class CustomYoutubeBoxActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener
{
    //Keys
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private YouTubePlayer youTubePlayer;
    private boolean isFullscreen;
    private int millis;
    private String mVideoId;
    private SongDao songDao;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        songDao = new SongDao(this);
        setContentView(R.layout.custom_youtube_box_activity);
        initSetUp(bundle);
        setRelativeLayout();
        setYouTubePlayerFragment();
        setRecyclerView();
        setContentTabs();
    }

    private void initSetUp(Bundle bundle)
    {
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_VIDEO_ID)) {
            mVideoId = extras.getString(KEY_VIDEO_ID);
        } else {
            finish();
        }
    }

    private void setRelativeLayout()
    {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_youtube_activity);
        relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    private void setYouTubePlayerFragment()
    {
        YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment.newInstance();
        youTubePlayerFragment.initialize("AIzaSyB7hLcRMs5KPZwElJnHBPK5DNmDqFxVy3s", this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (isLandScape()) {
            transaction.remove(youTubePlayerFragment).commit();
        } else {
            transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        }
    }

    private void setRecyclerView()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_recycle_view);
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setAdapter(getSongCardViewAdapter());
    }

    private LinearLayoutManager getLinearLayoutManager()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    private SongCardViewAdapter getSongCardViewAdapter()
    {
        SongCardViewAdapter songCarViewAdapter = new SongCardViewAdapter(getSong(), this);
        songCarViewAdapter.notifyDataSetChanged();
        return songCarViewAdapter;
    }

    private void setContentTabs()
    {
        String title = getIntent().getExtras().getString("title");
        SongContentLandScapeViewerPageAdapter songContentLandScapeViewerPageAdapter =
                new SongContentLandScapeViewerPageAdapter(getSupportFragmentManager(), title);
        setSlidingTab(songContentLandScapeViewerPageAdapter);
    }

    private void setSlidingTab(SongContentLandScapeViewerPageAdapter songContentLandScapeViewerPageAdapter)
    {
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.sliding_tab);
        tabs.setDistributeEvenly(false);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(android.R.color.background_dark);
            }
        });
        tabs.setVisibility(View.GONE);
        tabs.setViewPager(getViewPager(songContentLandScapeViewerPageAdapter));
    }

    @NonNull
    private ViewPager getViewPager(SongContentLandScapeViewerPageAdapter songContentLandScapeViewerPageAdapter)
    {
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(songContentLandScapeViewerPageAdapter);
        pager.setVisibility(isLandScape() ? View.VISIBLE : View.GONE);
        return pager;
    }

    private Song getSong()
    {
        Bundle extras = getIntent().getExtras();
        Song song = new Song();
        if (extras != null && extras.containsKey("title")) {
            song = songDao.findContentsByTitle(extras.getString("title"));
        }
        return song;
    }

    private boolean isLandScape()
    {
        return Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored)
    {
        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener()
            {
                @Override
                public void onFullscreen(boolean b)
                {
                    isFullscreen = b;
                }
            });
            if (mVideoId != null && !wasRestored) {
                youTubePlayer.loadVideo(mVideoId);
            }
            if (wasRestored) {
                youTubePlayer.seekToMillis(millis);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason)
    {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, "There was an error initializing the YouTubePlayer", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (youTubePlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, youTubePlayer.getCurrentTimeMillis());
            Log.i(this.getClass().getSimpleName(), "Video duration: " + youTubePlayer.getCurrentTimeMillis());
        }
    }

    @Override
    public void onBackPressed()
    {
        //If the Player is fullscreen then the transition crashes on L when navigating back to the MainActivity
        boolean finish = true;
        try {
            if (youTubePlayer != null) {
                if (isFullscreen) {
                    finish = false;
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener()
                    {
                        @Override
                        public void onFullscreen(boolean b)
                        {
                            //Wait until we are out of fullscreen before finishing this activity
                            if (!b) {
                                finish();
                            }
                        }
                    });
                    youTubePlayer.setFullscreen(false);
                }
                youTubePlayer.pause();
            }
        } catch (final IllegalStateException e) {
            //Crashlytics.logException(e);
        }

        if (finish) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyB7hLcRMs5KPZwElJnHBPK5DNmDqFxVy3s", this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider()
    {
        return YouTubePlayerFragment.newInstance();
    }

}
