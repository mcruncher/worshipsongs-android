package org.worshipsongs.fragment

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.SongContentViewActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.domain.ServiceSong
import org.worshipsongs.domain.Setting
import org.worshipsongs.domain.Song
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.service.PopupMenuService
import org.worshipsongs.service.SongService
import org.worshipsongs.service.UserPreferenceSettingService
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.PropertyUtils

import java.io.File
import java.util.ArrayList

/**
 * Author : Madasamy
 * Version : 3.x
 */

class ServiceSongsFragment : Fragment(), TitleAdapter.TitleAdapterListener<ServiceSong>, AlertDialogFragment.DialogListener
{
    private var songListView: ListView? = null
    private var titleAdapter: TitleAdapter<ServiceSong>? = null
    private var serviceName: String? = null
    private var songService: SongService? = null
    private var serviceSongs: ArrayList<ServiceSong>? = null
    private val titles = ArrayList<String>()
    private val preferenceSettingService = UserPreferenceSettingService()
    private var songContentViewListener: SongContentViewListener? = null
    private val popupMenuService = PopupMenuService()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        serviceName = arguments!!.getString(CommonConstants.SERVICE_NAME_KEY)
        songService = SongService(activity!!.applicationContext)
        setHasOptionsMenu(true)
        loadSongs()
    }

    private fun loadSongs()
    {
        Log.i(CLASS_NAME, "Preparing to find songs")
        val serviceFile = PropertyUtils.getPropertyFile(activity as Activity, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME)
        val property = PropertyUtils.getProperty(serviceName!!, serviceFile!!)
        val propertyValues = property.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        songService = SongService(activity!!.applicationContext)
        serviceSongs = ArrayList()
        for (title in propertyValues)
        {
            val song = songService!!.findContentsByTitle(title)
            serviceSongs!!.add(ServiceSong(title, song))
            titles.add(title)
        }
        Log.i(CLASS_NAME, "No of songs " + serviceSongs!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        songListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(serviceSongs!!)
        songListView!!.adapter = titleAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu)
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.setIconifiedByDefault(true)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.action_search)
        val image = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        val drawable = image.drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        val textChangeListener = object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextChange(newText: String): Boolean
            {
                titleAdapter!!.addObjects(songService!!.filteredServiceSongs(newText, serviceSongs))
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean
            {
                titleAdapter!!.addObjects(songService!!.filteredServiceSongs(query, serviceSongs))
                return true
            }
        }
        searchView.setOnQueryTextListener(textChangeListener)
        menu.getItem(0).isVisible = false
    }

    //Adapter listener methods
   override fun setViews(objects: Map<String, Any>, serviceSong: ServiceSong?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = songService!!.getTitle(preferenceSettingService.isTamil, serviceSong!!)
        titleTextView.setOnClickListener(SongOnClickListener(serviceSong!!))
        titleTextView.setOnLongClickListener(SongOnLongClickListener(serviceSong))

        val playImageView = objects[CommonConstants.PLAY_IMAGE_KEy] as ImageView?
        playImageView!!.visibility = if (isShowPlayIcon(serviceSong.song!!)) View.VISIBLE else View.GONE
        playImageView.setOnClickListener(imageOnClickListener(serviceSong.song, serviceSong.title!!))

        val optionsImageView = objects[CommonConstants.OPTIONS_IMAGE_KEY] as ImageView?
        optionsImageView!!.visibility = View.VISIBLE
        optionsImageView.setOnClickListener(imageOnClickListener(serviceSong.song, serviceSong.title!!))
    }

    internal fun isShowPlayIcon(song: Song): Boolean
    {
        try
        {
            val urlKey = song.urlKey
            return urlKey != null && urlKey.length > 0 && preferenceSettingService.isPlayVideo
        } catch (e: Exception)
        {
            return false
        }

    }

    private fun imageOnClickListener(song: Song?, title: String): View.OnClickListener
    {
        return View.OnClickListener { view ->
            if (song != null)
            {
                popupMenuService.showPopupmenu(activity as AppCompatActivity, view, song.title!!, true)
            } else
            {
                val bundle = Bundle()
                bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning))
                bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_song_not_available, "\"" + title + "\""))
                val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
                alertDialogFragment.setVisibleNegativeButton(false)
                alertDialogFragment.show(activity!!.supportFragmentManager, "WarningDialogFragment")
            }
        }
    }

    //Dialog listener methods
    override fun onClickPositiveButton(bundle: Bundle?, tag: String?)
    {
        if ("DeleteDialogFragment".equals(tag!!, ignoreCase = true))
        {
            removeSong(bundle!!.getString(CommonConstants.NAME_KEY))
        }
    }

    private fun removeSong(serviceSong: String?)
    {
        try
        {
            val serviceFile = PropertyUtils.getPropertyFile(activity!!, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME)
            PropertyUtils.removeSong(serviceFile!!, serviceName!!, serviceSong!!)
            serviceSongs!!.remove(getSongToBeRemoved(serviceSong, serviceSongs!!))
            titleAdapter!!.addObjects(serviceSongs!!)
        } catch (e: Exception)
        {
            Log.e(this.javaClass.name, "Error occurred while removing song", e)
        }

    }

    internal fun getSongToBeRemoved(title: String?, serviceSongs: List<ServiceSong>): ServiceSong?
    {
        for (serviceSong in serviceSongs)
        {
            if (serviceSong.title.equals(title!!, ignoreCase = true))
            {
                return serviceSong
            }
        }
        return null
    }

    override fun onClickNegativeButton()
    {
        //Do nothing
    }


    private inner class SongOnClickListener internal constructor(private val serviceSong: ServiceSong) : View.OnClickListener
    {

        override fun onClick(view: View)
        {
            if (serviceSong.song != null)
            {
                if (CommonUtils.isPhone(context!!))
                {
                    val intent = Intent(activity, SongContentViewActivity::class.java)
                    val bundle = Bundle()
                    val titles = ArrayList<String>()
                    titles.add(serviceSong.title!!)
                    bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles)
                    bundle.putInt(CommonConstants.POSITION_KEY, 0)
                    Setting.instance.position = 0
                    intent.putExtras(bundle)
                    activity!!.startActivity(intent)
                } else
                {
                    Setting.instance.position = titleAdapter!!.getPosition(serviceSong)
                    songContentViewListener!!.displayContent(serviceSong.title!!, titles, titleAdapter!!.getPosition(serviceSong))
                }
            } else
            {
                val bundle = Bundle()
                bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning))
                bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_song_not_available, "\"" + serviceSong.title + "\""))
                val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
                alertDialogFragment.setVisibleNegativeButton(false)
                alertDialogFragment.show(activity!!.supportFragmentManager, "WarningDialogFragment")
            }
        }
    }

    private inner class SongOnLongClickListener internal constructor(private val serviceSong: ServiceSong) : View.OnLongClickListener
    {

        override fun onLongClick(view: View): Boolean
        {
            val bundle = Bundle()
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.remove_favourite_song_title))
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.remove_favourite_song_message))
            bundle.putString(CommonConstants.NAME_KEY, serviceSong.title)
            val deleteAlertDialogFragment = AlertDialogFragment.newInstance(bundle)
            deleteAlertDialogFragment.setDialogListener(this@ServiceSongsFragment)
            deleteAlertDialogFragment.show(activity!!.supportFragmentManager, "DeleteDialogFragment")
            return true
        }
    }

    fun setSongContentViewListener(songContentViewListener: SongContentViewListener)
    {
        this.songContentViewListener = songContentViewListener
    }

    companion object
    {
        private val CLASS_NAME = ServiceSongsFragment::class.java.simpleName

        fun newInstance(bundle: Bundle): ServiceSongsFragment
        {
            val serviceSongsFragment = ServiceSongsFragment()
            serviceSongsFragment.arguments = bundle
            return serviceSongsFragment
        }
    }
}
