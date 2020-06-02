package org.worshipsongs.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.worshipsongs.CommonConstants
import org.worshipsongs.fragment.SongContentPortraitViewFragment
import org.worshipsongs.service.PresentationScreenService
import java.util.*

/**
 * author : Madasamy
 * version : 2.1.0
 */
class SongContentPortraitViewerPageAdapter(private val fragmentManager: FragmentManager, private val bundle: Bundle, private val presentationScreenService: PresentationScreenService) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_SET_USER_VISIBLE_HINT)
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