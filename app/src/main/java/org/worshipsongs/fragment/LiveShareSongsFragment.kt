package org.worshipsongs.fragment;

import android.content.Intent
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
import org.worshipsongs.activity.SongContentViewActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.domain.Setting
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.parser.LiveShareSongParser
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.LiveShareUtils
import java.io.File
import java.util.ArrayList

public class LiveShareSongsFragment : Fragment(), TitleAdapter.TitleAdapterListener<String>
{

    private var serviceName: String? = null
    private var songsListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null
    private var titles: MutableList<String> = ArrayList()
    private var liveShareSongParser = LiveShareSongParser()
    private var songContentViewListener: SongContentViewListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        serviceName = arguments!!.getString(CommonConstants.SERVICE_NAME_KEY)
        setHasOptionsMenu(true)
        loadSongs()
    }

    private fun loadSongs()
    {
        var serviceFilePath = LiveShareUtils.getServiceDirPath(context!!) + File.separator + serviceName
        titles = liveShareSongParser.parseTitles(serviceFilePath)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        songsListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(titles)
        songsListView!!.adapter = titleAdapter
    }


    override fun setViews(objects: Map<String, Any>, text: String?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = text!!
        titleTextView.setOnClickListener(SongOnClickListener(text))
    }

    private inner class SongOnClickListener internal constructor(private val songTitle: String) : View.OnClickListener
    {

        override fun onClick(view: View)
        {

            if (CommonUtils.isPhone(context!!))
            {
                val intent = Intent(activity, SongContentViewActivity::class.java)
                val bundle = Bundle()
                val titles = ArrayList<String>()
                titles.add(songTitle!!)
                bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles)
                bundle.putInt(CommonConstants.POSITION_KEY, 0)
                bundle.putString(CommonConstants.SERVICE_NAME_KEY, serviceName)
                Setting.instance.position = 0
                intent.putExtras(bundle)
                activity!!.startActivity(intent)
            } else
            {
                Setting.instance.position = titleAdapter!!.getPosition(songTitle)
                 songContentViewListener!!.displayContent(songTitle!!, titles, titleAdapter!!.getPosition(songTitle))
            }
        }
    }

    fun setSongContentViewListener(songContentViewListener: SongContentViewListener)
    {
        this.songContentViewListener = songContentViewListener
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
