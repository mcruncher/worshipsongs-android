package org.worshipsongs.activity


import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.WindowManager

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.adapter.SongContentLandScapeViewerPageAdapter
import org.worshipsongs.adapter.SongContentPortraitViewerPageAdapter
import org.worshipsongs.component.SlidingTabLayout
import org.worshipsongs.domain.Setting
import org.worshipsongs.fragment.ISongContentPortraitViewFragment
import org.worshipsongs.service.PresentationScreenService
import org.worshipsongs.service.UserPreferenceSettingService

/**
 * @Author : Seenivasan, Madasamy, Vignesh Palanisamy
 * @Version : 1.0
 */
class SongContentViewActivity : AbstractAppCompactActivity()
{
    private var userPreferenceSettingService: UserPreferenceSettingService? = null
    private var presentationScreenService: PresentationScreenService? = null
    private var isSectionView = true
    private var isTabView = true

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_content_view)
        initSetup(savedInstanceState)
        setView()
    }

    private fun initSetup(savedInstanceState: Bundle?)
    {
        userPreferenceSettingService = UserPreferenceSettingService()
        presentationScreenService = PresentationScreenService(this)
        if (userPreferenceSettingService!!.isKeepAwake)
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (savedInstanceState != null)
        {
            isSectionView = savedInstanceState.getBoolean("isSectionView")
            isTabView = savedInstanceState.getBoolean("isTabView")
        }
    }

    private fun setView()
    {
        if (Configuration.ORIENTATION_PORTRAIT == resources.configuration.orientation)
        {
            setPortraitView()
        } else
        {
            setLandscapeView()
        }
    }

    private fun setPortraitView()
    {
        setActionBar()
        val songContentPortraitViewPagerAdapter = SongContentPortraitViewerPageAdapter(supportFragmentManager, intent.extras!!, presentationScreenService!!)
        // Assigning ViewPager View and setting the adapter
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = songContentPortraitViewPagerAdapter
        // Assiging the Sliding Tab Layout View
        val tabs = findViewById<View>(R.id.tabs) as SlidingTabLayout
        //tabs.setVerticalScrollbarPosition();
        tabs.setDistributeEvenly(false)
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(object : SlidingTabLayout.TabColorizer
        {
            override fun getIndicatorColor(position: Int): Int
            {
                return resources.getColor(android.R.color.background_dark)
            }
        })

        tabs.visibility = View.GONE
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager)
        pager.currentItem = Setting.instance.position
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
            {
                Setting.instance.position = position
            }

            override fun onPageSelected(position: Int)
            {
                val fragment = songContentPortraitViewPagerAdapter.instantiateItem(pager, position) as ISongContentPortraitViewFragment
                fragment?.fragmentBecameVisible()
            }

            override fun onPageScrollStateChanged(state: Int)
            {
                //DO nothing when page scrolled
            }
        })
    }

    private fun setActionBar()
    {
        if (supportActionBar == null)
        {
            setCustomActionBar()
        }
    }

    private fun setLandscapeView()
    {
        val intent = intent
        val titleList = intent.extras!!.getStringArrayList(CommonConstants.TITLE_LIST_KEY)
        val songContentLandScapeViewerPageAdapter = SongContentLandScapeViewerPageAdapter(supportFragmentManager, titleList!![Setting.instance.position])
        // Assigning ViewPager View and setting the adapter
        val pager = findViewById<View>(R.id.land_pager) as ViewPager
        pager.adapter = songContentLandScapeViewerPageAdapter
        // Assiging the Sliding Tab Layout View
        val tabs = findViewById<View>(R.id.land_tabs) as SlidingTabLayout
        //tabs.setVerticalScrollbarPosition();
        tabs.setDistributeEvenly(false)
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(object : SlidingTabLayout.TabColorizer
        {
            override fun getIndicatorColor(position: Int): Int
            {
                return resources.getColor(android.R.color.background_dark)
            }
        })
        tabs.visibility = View.GONE
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager)
        Log.i(SongContentViewActivity::class.java.simpleName, "Finished")
    }

    public override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }


    override fun onStop()
    {
        super.onStop()
        presentationScreenService!!.onStop()
    }

    override fun onPause()
    {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
    }

}
