package org.worshipsongs.component

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.worshipsongs.R

import org.worshipsongs.fragment.SongsFragment
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.FragmentRegistry

/**
 * Author: madasamy.
 * version: 1.0.0
 */
class HomeViewerPageAdapter(fragmentManager: FragmentManager, private val activity: Activity, private val titles: List<String>, private val songContentViewListener: SongContentViewListener?) : FragmentPagerAdapter(fragmentManager)
{
    private val fragmentRegistry = FragmentRegistry()

    override fun getItem(position: Int): Fragment
    {
        val fragment = fragmentRegistry.findByTitle(activity, titles[position])
        if (fragment != null)
        {
            fragment.setListenerAndBundle(songContentViewListener, Bundle())
            return fragment as Fragment
        } else
        {
            return SongsFragment.newInstance(Bundle())
        }
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        return ""
    }

    override fun getCount(): Int
    {
        return titles.size
    }

}

