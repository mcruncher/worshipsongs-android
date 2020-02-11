package org.worshipsongs.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.FragmentRegistry
import java.util.*

/**
 * @author : Madasamy
 * @since : 3.x
 */

class HomeFragment : Fragment(), SongContentViewListener
{
    private var songContentFrameLayout: FrameLayout? = null
    private val fragmentRegistry = FragmentRegistry()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.home_layout, container, false) as View
        setContentViewFragment(view)
        setTabsFragment()
        return view
    }

    private fun setTabsFragment()
    {
        val fragmentManager = fragmentManager
        val existingHomeTabFragment = fragmentManager!!.findFragmentByTag(HomeTabFragment::class.java.simpleName) as HomeTabFragment?
        if (isNewTabSelected(existingHomeTabFragment))
        {
            val homeTabFragment = HomeTabFragment.newInstance()
            homeTabFragment.arguments = arguments
            if (songContentFrameLayout != null)
            {
                homeTabFragment.setSongContentViewListener(this)
            }
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.tabs_fragment, homeTabFragment, HomeTabFragment::class.java.simpleName)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    internal fun isNewTabSelected(homeTabFragment: HomeTabFragment?): Boolean
    {
        if (homeTabFragment != null)
        {
            val viewPager = homeTabFragment.view?.findViewById<ViewPager>(R.id.pager)
            val existingCurrentItem = viewPager?.currentItem
            if (arguments != null && arguments!!.containsKey(CommonConstants.TAB_SELECTED_ITEM_ID))
            {
                return arguments!!.getInt(CommonConstants.TAB_SELECTED_ITEM_ID) != existingCurrentItem
            }
        }
        return true
    }

    private fun setContentViewFragment(view: View)
    {
        songContentFrameLayout = view.findViewById(R.id.song_content_fragment)
    }

    override fun displayContent(title: String, titleList: List<String>, position: Int)
    {
        if (songContentFrameLayout != null)
        {
            val songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(title, ArrayList(titleList))
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.song_content_fragment, songContentPortraitViewFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onSaveInstanceState(state: Bundle)
    {
        super.onSaveInstanceState(state)
    }

    companion object
    {

        fun newInstance(): HomeFragment
        {
            return HomeFragment()
        }
    }


}
