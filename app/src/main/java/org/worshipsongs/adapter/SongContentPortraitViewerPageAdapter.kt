package org.worshipsongs.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log

import org.worshipsongs.CommonConstants
import org.worshipsongs.fragment.SongContentPortraitViewFragment
import org.worshipsongs.service.PresentationScreenService

import java.util.ArrayList

/**
 * author : Madasamy
 * version : 2.1.0
 */
class SongContentPortraitViewerPageAdapter(private val fragmentManager: FragmentManager, private val bundle: Bundle, private val presentationScreenService: PresentationScreenService) : FragmentStatePagerAdapter(fragmentManager)
{
    private val titles: ArrayList<String>?

    init
    {
        this.titles = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY)
    }

    override fun getItem(position: Int): Fragment
    {
        Log.i(this.javaClass.simpleName, "No of songs" + titles!!.size)
        val title = titles[position]
        bundle.putString(CommonConstants.TITLE_KEY, title)
        val songContentPortraitViewFragment = SongContentPortraitViewFragment.newInstance(bundle)
        songContentPortraitViewFragment.presentationScreenService = presentationScreenService
        return songContentPortraitViewFragment
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        return ""
    }

    override fun getCount(): Int
    {
        return titles!!.size
    }
}