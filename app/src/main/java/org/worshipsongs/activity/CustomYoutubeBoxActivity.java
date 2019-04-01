package org.worshipsongs.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter;
import org.worshipsongs.component.SlidingTabLayout;
import org.worshipsongs.service.SongService;
import org.worshipsongs.domain.Song;
import org.worshipsongs.R;
import org.worshipsongs.utils.ThemeUtils;

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
public class CustomYoutubeBoxActivity extends AbstractAppCompactActivity implements YouTubePlayer.OnInitializedListener
{
    //Keys
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    @Nullable
    private YouTubePlayer youTubePlayer;
    private boolean isFullscreen;
    private int millis;
    private String mVideoId;
    private SongService songService;

    @Override
    protected void onCreate(Bundle bundle)
    {
        initSetUp(bundle);
        super.onCreate(bundle);
        ThemeUtils.setTheme(this);
        songService = new SongService(this);
        setContentView(R.layout.custom_youtube_box_activity);
        setRelativeLayout();
        setYouTubePlayerFragment();
        setRecyclerView();
        setContentTabs();
    }

    private void initSetUp(Bundle bundle)
    {
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O &&
                android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O_MR1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
            bundle.remove("android:fragments");
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
        String title = getIntent().getExtras().getString(CommonConstants.TITLE_KEY);
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
            song = songService.findContentsByTitle(extras.getString("title"));
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
    public void onSaveInstanceState(Bundle outState)
    {
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
        youTubePlayer = null;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
        youTubePlayer = null;
        super.onStop();
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
                    youTubePlayer.setOnFullscreenListener(getOnFullscreenListener());
                    youTubePlayer.setFullscreen(false);
                }
                youTubePlayer.pause();
            }
        } catch (IllegalStateException e) {
            Log.e(CustomYoutubeBoxActivity.class.getSimpleName(), "Error", e);
        }

        if (finish) {
            super.onBackPressed();
        }
    }

    @NonNull
    private YouTubePlayer.OnFullscreenListener getOnFullscreenListener()
    {
        return new YouTubePlayer.OnFullscreenListener()
        {
            @Override
            public void onFullscreen(boolean b)
            {
                if (!b) {
                    finish();
                }
            }
        };
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
