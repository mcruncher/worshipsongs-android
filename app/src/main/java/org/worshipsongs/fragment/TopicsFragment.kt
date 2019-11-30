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
import org.worshipsongs.domain.Topics
import org.worshipsongs.domain.Type
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.service.TopicService
import org.worshipsongs.service.UserPreferenceSettingService
import org.worshipsongs.utils.CommonUtils

/**
 * Author : Madasamy
 * Version : 3.x
 */

class TopicsFragment : AbstractTabFragment(), TitleAdapter.TitleAdapterListener<Topics>, ITabFragment
{
    private var state: Parcelable? = null
    private var topicsService: TopicService? = null
    private var topicsList: List<Topics>? = null
    private var topicsListView: ListView? = null
    private var titleAdapter: TitleAdapter<Topics>? = null
    private val userPreferenceSettingService = UserPreferenceSettingService()

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
        topicsService = TopicService(activity!!.applicationContext)
        topicsList = topicsService!!.findAll()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false) as View
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        topicsListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(topicsService!!.filteredTopics("", topicsList!!))
        topicsListView!!.adapter = titleAdapter
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
                titleAdapter!!.addObjects(topicsService!!.filteredTopics(query, topicsList!!))
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean
            {
                titleAdapter!!.addObjects(topicsService!!.filteredTopics(newText, topicsList!!))
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
            outState.putParcelable(STATE_KEY, topicsListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        if (state != null)
        {
            topicsListView!!.onRestoreInstanceState(state)
        } else
        {
            titleAdapter!!.addObjects(topicsService!!.filteredTopics("", topicsList!!))
        }
    }

    override fun onPause()
    {
        state = topicsListView!!.onSaveInstanceState()
        super.onPause()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && topicsList != null)
        {
            CommonUtils.hideKeyboard(activity)
        }
    }


    private fun getTopicsName(topics: Topics): String
    {
        return if (userPreferenceSettingService.isTamil) topics.tamilName!! else topics.defaultName!!
    }

    override fun setViews(objects: Map<String, Any>, topics: Topics?)
    {
        val textView = objects[CommonConstants.TITLE_KEY] as TextView?
        textView!!.text = getTopicsName(topics!!)
        textView.setOnClickListener(getOnClickListener(topics))
        setCountView((objects[CommonConstants.SUBTITLE_KEY] as TextView?)!!, topics.noOfSongs.toString())
    }

    private fun getOnClickListener(topics: Topics): View.OnClickListener
    {
        return View.OnClickListener {
            val intent = Intent(context, SongListActivity::class.java)
            intent.putExtra(CommonConstants.TYPE, Type.TOPICS.name)
            intent.putExtra(CommonConstants.TITLE_KEY, getTopicsName(topics))
            intent.putExtra(CommonConstants.ID, topics.id)
            startActivity(intent)
        }
    }

    override fun defaultSortOrder(): Int
    {
        return 2
    }

    override val title: String
        get()
        {
            return "categories"
        }

    override fun checked(): Boolean
    {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
    {
        // Do nothing
    }

    companion object
    {
        private val STATE_KEY = "listViewState"
    }

}
