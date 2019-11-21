package org.worshipsongs.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.component.HomeViewerPageAdapter
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.FragmentRegistry

/**
 * author:Seenivasan, Madasamy
 * version:2.1.0
 */
class HomeTabFragment : Fragment()
{
    private var songContentViewListener: SongContentViewListener? = null
    private var preferences: SharedPreferences? = null
    private val fragmentRegistry = FragmentRegistry()
    private var titles: List<String>? = null

    @SuppressLint("ShowToast")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.home_tab_layout, container, false) as View
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        titles = fragmentRegistry.getTitles(activity)
        val adapter = HomeViewerPageAdapter(childFragmentManager, activity!!, titles!!, songContentViewListener!!)
        adapter.notifyDataSetChanged()

        // Assigning ViewPager View and setting the adapter
        val pager = view.findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = adapter
        // Assiging the Sliding Tab Layout View
        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val typedValue = TypedValue()
        activity!!.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        tabLayout.setBackgroundColor(typedValue.data)
        tabLayout.setupWithViewPager(pager)
        setTabIcon(tabLayout)
        // setSelectedTab(pager);
        displayPlayListTab(pager)
        return view
    }

    private fun setTabIcon(tabLayout: TabLayout)
    {
        for (i in titles!!.indices)
        {
            val drawable = activity!!.resources.getIdentifier("ic_" + titles!![i], "drawable", WorshipSongApplication.getContext().packageName)
            tabLayout.getTabAt(i)!!.icon = resources.getDrawable(drawable)
        }
    }

    private fun displayPlayListTab(pager: ViewPager)
    {
        if (arguments != null && arguments!!.getInt(CommonConstants.FAVOURITES_KEY) > 0)
        {
            val titles = fragmentRegistry.getTitles(activity)
            if (titles.contains("playlists"))
            {
                pager.currentItem = titles.indexOf("playlists")
            }
        } else if (arguments != null && arguments!!.getInt(CommonConstants.FAVOURITES_KEY) == 0)
        {
            Toast.makeText(activity, R.string.message_songs_not_existing, Toast.LENGTH_LONG).show()
        }
    }

    fun setSongContentViewListener(songContentViewListener: SongContentViewListener)
    {
        this.songContentViewListener = songContentViewListener
    }

    companion object
    {

        fun newInstance(): HomeTabFragment
        {
            return HomeTabFragment()
        }
    }
}