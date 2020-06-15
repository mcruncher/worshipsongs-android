package org.worshipsongs.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.adapter.SongCardViewAdapter
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter
import org.worshipsongs.component.SlidingTabLayout
import org.worshipsongs.component.SlidingTabLayout.TabColorizer
import org.worshipsongs.domain.Song
import org.worshipsongs.service.SongService
import org.worshipsongs.utils.PropertyUtils
import org.worshipsongs.utils.ThemeUtils

/***********************************************************************************
 * The MIT License (MIT)
 *
 *
 * Copyright (c) 2015 Scott Cooper
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * This Activity shows how the YouTubePlayerView can be used to create a "Lightbox" similar to that of the
 * StandaloneYouTubePlayer. Using this method, we can improve upon it by performing transitions and allowing for
 * custom behaviour, such as closing when the user clicks anywhere outside the player
 * We manage to avoid rebuffering the video by setting some configchange flags on this activities declaration in the manifest.
 */
class CustomYoutubeBoxActivity : AbstractAppCompactActivity(), YouTubePlayer.OnInitializedListener
{

    private var youTubePlayer: YouTubePlayer? = null
    private var isFullscreen: Boolean = false
    private var millis: Int = 0
    private var mVideoId: String? = null
    private var songService: SongService? = null

    private val linearLayoutManager: LinearLayoutManager
        get()
        {
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            return linearLayoutManager
        }

    private val songCardViewAdapter: SongCardViewAdapter
        get()
        {
            val songCarViewAdapter = SongCardViewAdapter(song!!, this)
            songCarViewAdapter.notifyDataSetChanged()
            return songCarViewAdapter
        }

    private val song: Song?
        get()
        {
            val extras = intent.extras
            var song: Song? = Song()
            if (extras != null && extras.containsKey("title"))
            {
                song = songService!!.findContentsByTitle(extras.getString("title")!!)
            }
            return song
        }

    private val isLandScape: Boolean
        get() = Configuration.ORIENTATION_LANDSCAPE == resources.configuration.orientation

    private val onFullscreenListener: YouTubePlayer.OnFullscreenListener
        get() = YouTubePlayer.OnFullscreenListener { b ->
            if (!b)
            {
                finish()
            }
        }

    private val youTubePlayerProvider: YouTubePlayer.Provider
        get() = YouTubePlayerFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        initSetUp(savedInstanceState)
        super.onCreate(savedInstanceState)
        ThemeUtils.setTheme(this)
        songService = SongService(this)
        setContentView(R.layout.custom_youtube_box_activity)
        setRelativeLayout()
        setYouTubePlayerFragment()
        setRecyclerView()
        setContentTabs()
    }

    private fun initSetUp(bundle: Bundle?)
    {
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O && android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O_MR1)
        {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (bundle != null)
        {
            millis = bundle.getInt(KEY_VIDEO_TIME)
            Log.i(this.javaClass.simpleName, "Video time $millis")
            bundle.remove("android:fragments")
        }

        val extras = intent.extras
        if (extras != null && extras.containsKey(KEY_VIDEO_ID))
        {
            mVideoId = extras.getString(KEY_VIDEO_ID)
        } else
        {
            finish()
        }
    }

    private fun setRelativeLayout()
    {
        val relativeLayout = findViewById<View>(R.id.relativeLayout_youtube_activity) as RelativeLayout
        relativeLayout.setOnClickListener { onBackPressed() }
    }

    private fun setYouTubePlayerFragment()
    {
        val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
        youTubePlayerFragment.initialize(PropertyUtils.getYouTubeApiKey(this.applicationContext!!), this)
        val transaction = supportFragmentManager.beginTransaction()
        if (isLandScape)
        {
            transaction.replace(R.id.youtube_fragment, youTubePlayerFragment).commit()
        } else
        {
            transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit()
        }
    }

    private fun setRecyclerView()
    {
        val recyclerView = findViewById<View>(R.id.content_recycle_view) as RecyclerView
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = songCardViewAdapter
    }

    private fun setContentTabs()
    {
        val title = intent.extras!!.getString(CommonConstants.TITLE_KEY)
        val songContentLandScapeViewerPageAdapter = SongContentLandScapeViewerPageAdapter(supportFragmentManager, title!!)
        setSlidingTab(songContentLandScapeViewerPageAdapter)
    }

    private fun setSlidingTab(songContentLandScapeViewerPageAdapter: SongContentLandScapeViewerPageAdapter)
    {
        val tabs = findViewById<View>(R.id.sliding_tab) as SlidingTabLayout
        tabs.setDistributeEvenly(false)
        tabs.setCustomTabColorizer(object : TabColorizer
        {
            override fun getIndicatorColor(position: Int): Int
            {
                return resources.getColor(android.R.color.background_dark)
            }
        })
        tabs.visibility = View.GONE
        tabs.setViewPager(getViewPager(songContentLandScapeViewerPageAdapter))
    }

    private fun getViewPager(songContentLandScapeViewerPageAdapter: SongContentLandScapeViewerPageAdapter): ViewPager
    {
        val pager = findViewById<View>(R.id.view_pager) as ViewPager
        pager.adapter = songContentLandScapeViewerPageAdapter
        pager.visibility = if (isLandScape) View.VISIBLE else View.GONE
        return pager
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean)
    {
        if (Configuration.ORIENTATION_PORTRAIT == resources.configuration.orientation)
        {
            this.youTubePlayer = youTubePlayer
            youTubePlayer.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI)
            youTubePlayer.setShowFullscreenButton(false)
            youTubePlayer.setOnFullscreenListener { b -> isFullscreen = b }
            if (mVideoId != null && !wasRestored)
            {
                youTubePlayer.loadVideo(mVideoId)
            }
            if (wasRestored)
            {
                youTubePlayer.seekToMillis(millis)
            }
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult)
    {
        if (errorReason.isUserRecoverableError)
        {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else
        {
            Toast.makeText(this, "There was an error initializing the YouTubePlayer", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle)
    {
        if (youTubePlayer != null)
        {
            youTubePlayer!!.release()
        }
        youTubePlayer = null
        super.onSaveInstanceState(outState)
    }

    public override fun onStop()
    {
        if (youTubePlayer != null)
        {
            youTubePlayer!!.release()
        }
        youTubePlayer = null
        super.onStop()
    }

    override fun onBackPressed()
    {
        //If the Player is fullscreen then the transition crashes on L when navigating back to the MainActivity
        var finish = true
        try
        {
            if (youTubePlayer != null)
            {
                if (isFullscreen)
                {
                    finish = false
                    youTubePlayer!!.setOnFullscreenListener(onFullscreenListener)
                    youTubePlayer!!.setFullscreen(false)
                }
                youTubePlayer!!.pause()
            }
        } catch (e: Exception)
        {
            Log.e(CustomYoutubeBoxActivity::class.java.simpleName, "Error", e)
        }

        if (finish)
        {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_DIALOG_REQUEST)
        {
            // Retry initialization if user performed a recovery action
            youTubePlayerProvider.initialize(PropertyUtils.getYouTubeApiKey(this.applicationContext!!), this)
        }
    }

    companion object
    {
        val KEY_VIDEO_ID = "KEY_VIDEO_ID"
        private val KEY_VIDEO_TIME = "KEY_VIDEO_TIME"
        private val RECOVERY_DIALOG_REQUEST = 1
    }

}
