package org.worshipsongs.activity;

import android.os.Bundle
import android.widget.FrameLayout
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.fragment.FavouriteSongsFragment
import org.worshipsongs.fragment.LiveShareSongsFragment
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.service.PresentationScreenService

public class LiveShareSongsActivity: AbstractAppCompactActivity(), SongContentViewListener
{
    private var songContentFrameLayout: FrameLayout? = null
    private var presentationScreenService: PresentationScreenService? = null
    private val serviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)
        presentationScreenService = PresentationScreenService(this)
        setActionBar()
        setContentViewFragment()
        setTabsFragment()
    }

    private fun setActionBar()
    {
        setCustomActionBar()
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getFavouriteName()
    }

    private fun setContentViewFragment()
    {
        songContentFrameLayout = findViewById<FrameLayout>(R.id.song_content_fragment)
    }

    private fun setTabsFragment()
    {
        val fragmentManager = supportFragmentManager
        val existingServiceSongsFragment = fragmentManager.findFragmentByTag(LiveShareSongsFragment::class.java.simpleName) as FavouriteSongsFragment?
        if (existingServiceSongsFragment == null)
        {
            val bundle = Bundle()
            bundle.putString(CommonConstants.SERVICE_NAME_KEY, getFavouriteName())
            val liveShareSongsFragment = LiveShareSongsFragment.newInstance(bundle)
           // liveShareSongsFragment.setSongContentViewListener(this)
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.tabs_fragment, liveShareSongsFragment, LiveShareSongsFragment::class.java.simpleName)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun getFavouriteName(): String
    {
        return serviceName ?: intent.getStringExtra(CommonConstants.SERVICE_NAME_KEY)!!
    }


    override fun displayContent(title: String, titleList: List<String>, position: Int)
    {
        if (songContentFrameLayout != null)
        {
//            val titles = ArrayList<String>()
//            titles.add(title)
//            val songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, titles)
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
        }
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

    override fun onBackPressed()
    {
        super.onBackPressed()
        finish()
    }
}
