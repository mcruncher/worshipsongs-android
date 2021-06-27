package org.worshipsongs.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.LiveShareSongsActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.task.AsyncLiveShareTask
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.UnzipUtils
import java.util.*

class LiveShareServiceFragment : Fragment(), ITabFragment, TitleAdapter.TitleAdapterListener<String>
{
    private var infoTextView: TextView? = null
    private var state: Parcelable? = null
    private var services: MutableList<String> = ArrayList()
    private var refreshServiceListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY)
        }
        setHasOptionsMenu(true)
        setServices()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
        setInfoTextView(view)
        setSwipeRefreshLayout(view)
        setListView(view)
        return view
    }

    private fun setInfoTextView(view: View)
    {
        infoTextView = view.findViewById<View>(R.id.info_text_view) as TextView
        infoTextView!!.text = getString(R.string.favourite_info_message_)
        infoTextView!!.setLineSpacing(0f, 1.2f)
        infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setSwipeRefreshLayout(view: View)
    {
        val swipeRefreshView = view!!.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        swipeRefreshView.visibility = View.VISIBLE
        swipeRefreshView.setOnRefreshListener {
            AsyncLiveShareTask(activity as AppCompatActivity).execute()
            if (titleAdapter != null)
            {
                setServices()
                titleAdapter!!.addObjects(services)
            }
            swipeRefreshView.isRefreshing = false
        }
        refreshServiceListView = swipeRefreshView.findViewById<View>(R.id.refresh_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(services)
        refreshServiceListView!!.adapter = titleAdapter
    }

    private fun setServices()
    {
        if (context != null)
        {
            val serviceDir = "/data/data/" + context!!.applicationContext.packageName + "/databases/service"
            services = UnzipUtils.getServiceNames(serviceDir)
        }
    }

    private fun setListView(view: View)
    {
        val serviceListView = view.findViewById<View>(R.id.song_list_view) as ListView
        serviceListView!!.visibility = View.GONE
    }

    override val title: String
        get() = "live_share"

    override fun defaultSortOrder(): Int
    {
        return 5
    }

    override fun checked(): Boolean
    {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
    {

    }

    override fun setViews(objects: Map<String, Any>, text: String?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = text!!
        titleTextView.setOnClickListener(TextViewOnClickListener(text))
    }

    private inner class TextViewOnClickListener internal constructor(private val serviceName: String) : View.OnClickListener
    {

        override fun onClick(v: View)
        {
            val intent = Intent(activity, LiveShareSongsActivity::class.java)
            intent.putExtra(CommonConstants.SERVICE_NAME_KEY, serviceName)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        if (this.isAdded && refreshServiceListView != null)
        {
            outState.putParcelable(CommonConstants.STATE_KEY, refreshServiceListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        setServices()
        refreshListView()
    }

    private fun refreshListView()
    {
        if (state != null)
        {
            refreshServiceListView!!.onRestoreInstanceState(state)
        } else if (titleAdapter != null)
        {
            titleAdapter!!.addObjects(services)
            infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
        {
            hideKeyboard()
            setServices()
            refreshListView()
        }
    }

    private fun hideKeyboard()
    {
        if (activity != null)
        {
            CommonUtils.hideKeyboard(activity)
        }
    }

}