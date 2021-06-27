package org.worshipsongs.fragment;

import android.os.Bundle
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
import org.worshipsongs.utils.UnzipUtils
import java.io.File
import java.util.ArrayList

public class LiveShareSongsFragment : Fragment(), TitleAdapter.TitleAdapterListener<String>
{

    private var serviceName: String? = null
    private var songsListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null
    private var songs: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        serviceName = arguments!!.getString(CommonConstants.SERVICE_NAME_KEY)
        setHasOptionsMenu(true)
        loadSongs()
    }

    private fun loadSongs()
    {
        //"/data/data/" + context.applicationContext.packageName + "/databases/service"
        var serviceFilePath = "/data/data/" + context!!.applicationContext.packageName + "/databases/service/" + File.separator + serviceName
        songs = UnzipUtils.getSongs(serviceFilePath)
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
        songsListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(songs)
        songsListView!!.adapter = titleAdapter
    }


    override fun setViews(objects: Map<String, Any>, text: String?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = text!!
    }

    companion object
    {
        fun newInstance(bundle: Bundle): LiveShareSongsFragment
        {
            val liveShareSongsFragment = LiveShareSongsFragment()
            liveShareSongsFragment.arguments = bundle
            return liveShareSongsFragment
        }
    }
}
