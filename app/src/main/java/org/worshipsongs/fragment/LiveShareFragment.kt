package org.worshipsongs.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.task.AsyncLiveShareTask
import org.worshipsongs.utils.UnzipUtils
import java.util.ArrayList

class LiveShareFragment : Fragment(), ITabFragment, TitleAdapter.TitleAdapterListener<String>
{
    private var state: Parcelable? = null
    private var services: MutableList<String> = ArrayList()
    private var serviceListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY)
        }
        setHasOptionsMenu(true)
        initSetup()
    }

    private fun initSetup()
    {
        AsyncLiveShareTask(activity as AppCompatActivity).execute()
        var serviceDir = "/data/data/" + context!!.applicationContext.packageName + "/databases/service"
        services = UnzipUtils.getServiceNames(serviceDir)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
        //  setInfoTextView(view)
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        serviceListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(services)
        serviceListView!!.adapter = titleAdapter
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
        //titleTextView.setOnClickListener(TextViewOnClickListener(text))
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        if (this.isAdded && serviceListView !=null)
        {
            outState.putParcelable(CommonConstants.STATE_KEY, serviceListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        initSetup()
        refreshListView()
    }

    private fun refreshListView()
    {
        if (state != null)
        {
            serviceListView!!.onRestoreInstanceState(state)
        } else if (titleAdapter != null)
        {
            titleAdapter!!.addObjects(services)
//            infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}