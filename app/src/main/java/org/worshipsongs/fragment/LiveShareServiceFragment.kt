package org.worshipsongs.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.LiveShareSongsActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.parser.LiveShareSongParser
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.task.AsyncLiveShareTask
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.LiveShareUtils
import java.util.*


class LiveShareServiceFragment : Fragment(), ITabFragment, TitleAdapter.TitleAdapterListener<String>
{
    private var infoScrollView: NestedScrollView? = null
    private var state: Parcelable? = null
    private var services: MutableList<String> = ArrayList()
    private var refreshServiceListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null
    private var liveShareSongParser = LiveShareSongParser()
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        setHasOptionsMenu(true)
        setServices()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.live_share_service_layout, container, false)
        setSwipeRefreshLayout(view)
        return view
    }

    private fun setSwipeRefreshLayout(view: View)
    {
        val swipeRefreshView = view!!.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        swipeRefreshView.visibility = View.VISIBLE
        swipeRefreshView.setOnRefreshListener {
            doFetchServices()
            if (titleAdapter != null)
            {
                setServices()
                refreshListView()
            }
            swipeRefreshView.isRefreshing = false
        }
        setInfoScrollView(swipeRefreshView)
        setListView(swipeRefreshView)
    }

    private fun setInfoScrollView(view: View)
    {
        infoScrollView = view.findViewById<NestedScrollView>(R.id.info_scroll_view)
        var infoTextView = infoScrollView!!.findViewById<TextView>(R.id.info_text_view)
        infoTextView!!.text = Html.fromHtml(getString(R.string.live_share_info_message))
        infoTextView!!.setLineSpacing(0f, 1.2f)
        infoScrollView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setListView(swipeRefreshView: SwipeRefreshLayout)
    {
        refreshServiceListView = swipeRefreshView.findViewById<View>(R.id.refresh_list_view) as ListView
        refreshServiceListView!!.visibility = if (services.isEmpty()) View.GONE else View.VISIBLE
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(services)
        refreshServiceListView!!.adapter = titleAdapter
    }

    private fun doFetchServices()
    {
        if (CommonUtils.isWifiOrMobileDataConnectionExists(activity as AppCompatActivity))
        {
            val liveSharePathKey = sharedPreferences!!.getString(CommonConstants.LIVE_SHARE_PATH_KEY, "")
            if (StringUtils.isNotBlank(liveSharePathKey))
            {
                AsyncLiveShareTask(activity as AppCompatActivity, this).execute()
            } else
            {
                Toast.makeText(context, getString(R.string.message_warning_live_share_path), Toast.LENGTH_LONG).show()
            }
        } else
        {
            Toast.makeText(context, getString(R.string.message_warning_internet_connection), Toast.LENGTH_LONG).show()
        }
    }

    private fun setServices()
    {
        if (context != null)
        {
            services = LiveShareUtils.getServices(LiveShareUtils.getServiceDirPath(context!!))
        }
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
            infoScrollView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
            refreshServiceListView!!.visibility = if (services.isEmpty()) View.GONE else View.VISIBLE
            titleAdapter!!.addObjects(services)
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