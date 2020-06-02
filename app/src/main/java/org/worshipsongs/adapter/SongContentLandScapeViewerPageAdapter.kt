package org.worshipsongs.adapter

import android.os.Bundle

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import org.worshipsongs.CommonConstants
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.activity.SongContentViewActivity
import org.worshipsongs.service.SongService
import org.worshipsongs.domain.Song
import org.worshipsongs.fragment.SongContentLandscapeViewFragment
import org.worshipsongs.service.AuthorService

/**
 * @author: Madasamy
 * @since: 2.1.0
 */
class SongContentLandScapeViewerPageAdapter(fragmentManager: FragmentManager, private val title: String) : FragmentStatePagerAdapter(fragmentManager)
{

    private val songService = SongService(WorshipSongApplication.context!!)
    private val authorService = AuthorService(WorshipSongApplication.context!!)
    private var contents: List<String>? = null
    private var authorName: String? = null
    private var song: Song? = null


    init
    {
        Log.i(SongContentLandScapeViewerPageAdapter::class.java.simpleName, "Song content land scape view")
        initSetUp()
    }

    fun initSetUp()
    {
        song = songService.findContentsByTitle(title)
        contents = song!!.contents
        Log.i(SongContentLandScapeViewerPageAdapter::class.java.simpleName, "Song content size" + contents!!.size)
        authorName = authorService.findAuthorNameByTitle(title)
    }

    override fun getItem(position: Int): Fragment
    {

        val songContentLandscapeViewFragment = SongContentLandscapeViewFragment()
        val bundle = Bundle()
        val content = contents!![position]
        bundle.putString("content", content)
        bundle.putString(CommonConstants.TITLE_KEY, title)
        bundle.putString("authorName", authorName)
        bundle.putString("position", position.toString())
        bundle.putString("size", contents!!.size.toString())
        bundle.putString("chord", song!!.chord)
        songContentLandscapeViewFragment.arguments = bundle
        return songContentLandscapeViewFragment
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        return ""
    }

    override fun getCount(): Int
    {
        return contents!!.size
    }
}
