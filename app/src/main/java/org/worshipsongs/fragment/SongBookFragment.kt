package org.worshipsongs.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.SongListActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.domain.SongBook
import org.worshipsongs.domain.Type
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.service.SongBookService

/**
 * Author : Madasamy
 * Version : 3.x
 */

class SongBookFragment : AbstractTabFragment(), TitleAdapter.TitleAdapterListener<SongBook>, ITabFragment
{
    private var state: Parcelable? = null
    private var songBookService: SongBookService? = null
    private var songBookList: List<SongBook>? = null
    private var songBookListView: ListView? = null
    private var titleAdapter: TitleAdapter<SongBook>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            state = savedInstanceState.getParcelable(STATE_KEY)
        }
        setHasOptionsMenu(true)
        initSetUp()
    }

    private fun initSetUp()
    {
        songBookService = SongBookService(activity)
        songBookList = songBookService!!.findAll()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false) as View
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        songBookListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(songBookService!!.filteredSongBooks("", songBookList))
        songBookListView!!.adapter = titleAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu)
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.menu_search).actionView as android.support.v7.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.action_search)
        val image = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        val drawable = image.drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                titleAdapter!!.addObjects(songBookService!!.filteredSongBooks(query, songBookList))
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean
            {
                titleAdapter!!.addObjects(songBookService!!.filteredSongBooks(newText, songBookList))
                return true
            }
        })
        menu.getItem(0).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        if (this.isAdded)
        {
            outState.putParcelable(STATE_KEY, songBookListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        if (state != null)
        {
            songBookListView!!.onRestoreInstanceState(state)
        } else
        {
            titleAdapter!!.addObjects(songBookService!!.filteredSongBooks("", songBookList))
        }
    }

    override fun onPause()
    {
        state = songBookListView!!.onSaveInstanceState()
        super.onPause()
    }

   override fun setViews(objects: Map<String, Any>, songBook: SongBook?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = songBook!!.name
        titleTextView.setOnClickListener(getOnClickListener(songBook))
        setCountView((objects[CommonConstants.SUBTITLE_KEY] as TextView?)!!, songBook.noOfSongs.toString())
    }

    private fun getOnClickListener(songBook: SongBook): View.OnClickListener
    {
        return View.OnClickListener {
            val intent = Intent(context, SongListActivity::class.java)
            intent.putExtra(CommonConstants.TYPE, Type.SONG_BOOK.name)
            intent.putExtra(CommonConstants.TITLE_KEY, songBook.name)
            intent.putExtra(CommonConstants.ID, songBook.id)
            startActivity(intent)
        }
    }

    override fun defaultSortOrder(): Int
    {
        return 3
    }

    override fun getTitle(): String
    {
        return "song_books"
    }

    override fun checked(): Boolean
    {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener, bundle: Bundle)
    {
        // Do nothing
    }

    companion object
    {
        private val STATE_KEY = "listViewState"

        fun newInstance(): SongBookFragment
        {
            return SongBookFragment()
        }
    }


}
