package org.worshipsongs.activity


import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.fragment.SongContentPortraitViewFragment
import org.worshipsongs.fragment.SongsFragment
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.service.PresentationScreenService
import java.util.*

/**
 * author: Seenivasan, Madasamy
 * version: 2.1.0
 */
class SongListActivity : AbstractAppCompactActivity(), SongContentViewListener
{
    private var presentationScreenService: PresentationScreenService? = null
    private var songContentFrameLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)
        initSetUp()
        setContentViewFragment()
        setFragment()
    }

    private fun initSetUp()
    {
        presentationScreenService = PresentationScreenService(this)
        setActionBar()
    }

    private fun setActionBar()
    {
        setCustomActionBar()
        val title = intent.getStringExtra(CommonConstants.TITLE_KEY)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = title
    }

    private fun setContentViewFragment()
    {
        songContentFrameLayout = findViewById(R.id.song_content_fragment)
    }

    private fun setFragment()
    {
        val intent = intent
        val bundle = Bundle()
        bundle.putString(CommonConstants.TYPE, intent.getStringExtra(CommonConstants.TYPE))
        bundle.putInt(CommonConstants.ID, intent.getIntExtra(CommonConstants.ID, 0))
        val songsFragment = SongsFragment.newInstance(bundle)
        if (songContentFrameLayout != null)
        {
            songsFragment.setListenerAndBundle(this, Bundle())
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.tabs_fragment, songsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun displayContent(title: String, titleList: List<String>, position: Int)
    {
        if (songContentFrameLayout != null)
        {
            val songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, ArrayList(titleList))
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        super.onPrepareOptionsMenu(menu)
        return true
    }

    override fun onPause()
    {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }

    override fun onStop()
    {
        super.onStop()
        presentationScreenService!!.onStop()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        finish()
    }
    
}
