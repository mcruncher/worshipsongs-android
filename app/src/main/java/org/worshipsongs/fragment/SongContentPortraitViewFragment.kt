package org.worshipsongs.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.youtube.player.YouTubePlayer
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.activity.CustomYoutubeBoxActivity
import org.worshipsongs.adapter.PresentSongCardViewAdapter
import org.worshipsongs.domain.Setting
import org.worshipsongs.domain.Song
import org.worshipsongs.service.*
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.PermissionUtils
import java.util.*

/**
 * @author: Madasamy, Vignesh Palanisamy
 * @since: 1.0.0
 */

class SongContentPortraitViewFragment : Fragment(), ISongContentPortraitViewFragment
{
    private var title: String? = ""
    private var tilteList: ArrayList<String>? = ArrayList()
    private var millis: Int = 0
    private val youTubePlayer: YouTubePlayer? = null
    private val preferenceSettingService = UserPreferenceSettingService()
    private val songDao = SongService(WorshipSongApplication.context!!)
    private val authorService = AuthorService(WorshipSongApplication.context!!)
    private var popupMenuService: PopupMenuService? = null
    private var floatingActionMenu: FloatingActionsMenu? = null
    private var song: Song? = null
    private var listView: ListView? = null
    private var presentSongCardViewAdapter: PresentSongCardViewAdapter? = null
    private var nextButton: FloatingActionButton? = null
    private var previousButton: FloatingActionButton? = null
    private var presentSongFloatingButton: FloatingActionButton? = null
    //    @Override
    //    public void onAttach(Context context)
    //    {
    //        super.onAttach(context);
    //        Log.i(SongContentPortraitViewFragment.class.getSimpleName(), "" + context);
    //        if (context instanceof SongContentViewActivity) {
    //            activity = (SongContentViewActivity) context;
    //        }
    //    }

    var presentationScreenService: PresentationScreenService? = null
    private val customTagColorService = CustomTagColorService()

    private val isPresentSong: Boolean
        get() = presentationScreenService != null && presentationScreenService!!.presentation != null

    private val songBookNumber: String
        get()
        {
            try
            {
                if (arguments!!.containsKey(CommonConstants.SONG_BOOK_NUMBER_KEY))
                {
                    val songBookNumber = arguments!!.getInt(CommonConstants.SONG_BOOK_NUMBER_KEY, 0)
                    return if (songBookNumber > 0) "$songBookNumber. " else ""
                }
            } catch (ex: Exception)
            {
                Log.e(SongContentPortraitViewFragment::class.java.simpleName, "Error ", ex)
            }

            return ""
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(CommonUtils.isPhone(WorshipSongApplication.context!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.song_content_portrait_view, container, false)
        initSetUp()
        setListView(view, song!!)
        setFloatingActionMenu(view, song!!)
        setNextButton(view)
        setPreviousButton(view)
        view.setOnTouchListener(SongContentPortraitViewTouchListener())
        onBecameVisible(song)
        return view
    }


    private fun initSetUp()
    {
        showStatusBar()
        val bundle = arguments
        title = bundle!!.getString(CommonConstants.TITLE_KEY)
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY)
        if (bundle != null)
        {
            millis = bundle.getInt(KEY_VIDEO_TIME)
            Log.i(this.javaClass.simpleName, "Video time $millis")
        }
        setSong()

    }

    private fun showStatusBar()
    {
        if (CommonUtils.isPhone(context!!))
        {
            if (Build.VERSION.SDK_INT < 16)
            {
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else
            {
                val decorView = activity!!.window.decorView
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        val appCompatActivity = activity as AppCompatActivity?
        if (appCompatActivity!!.supportActionBar != null)
        {
            appCompatActivity.supportActionBar!!.show()
            appCompatActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(CommonUtils.isPhone(WorshipSongApplication.context!!))
        }
    }

    private fun setSong()
    {
        song = songDao.findContentsByTitle(title!!)
        if (song == null)
        {
            song = Song(title!!)
            val contents = ArrayList<String>()
            contents.add(getString(R.string.message_song_not_available, "\"" + title + "\""))
            song!!.contents = contents
        }
        song!!.authorName = authorService.findAuthorNameByTitle(title!!)
    }

    private fun setListView(view: View, song: Song)
    {
        listView = view.findViewById<View>(R.id.content_list) as ListView
        presentSongCardViewAdapter = PresentSongCardViewAdapter(activity!!, song.contents!!)
        listView!!.adapter = presentSongCardViewAdapter
        listView!!.onItemClickListener = ListViewOnItemClickListener()
        listView!!.onItemLongClickListener = ListViewOnItemLongClickListener()
    }

    private fun setFloatingActionMenu(view: View, song: Song)
    {
        floatingActionMenu = view.findViewById<View>(R.id.floating_action_menu) as FloatingActionsMenu
        if (isPlayVideo(song.urlKey) && isPresentSong)
        {
            floatingActionMenu!!.visibility = View.VISIBLE
            floatingActionMenu!!.setOnFloatingActionsMenuUpdateListener(object : FloatingActionsMenu.OnFloatingActionsMenuUpdateListener
            {
                override fun onMenuExpanded()
                {
                    val color = R.color.gray_transparent
                    setListViewForegroundColor(ContextCompat.getColor(activity!!, color))
                }

                override fun onMenuCollapsed()
                {
                    val color = 0x00000000
                    setListViewForegroundColor(color)
                }
            })
            setPlaySongFloatingMenuButton(view, song.urlKey!!)
            setPresentSongFloatingMenuButton(view)
        } else
        {
            floatingActionMenu!!.visibility = View.GONE
            if (isPresentSong)
            {
                setPresentSongFloatingButton(view)
            }
            if (isPlayVideo(song.urlKey))
            {
                setPlaySongFloatingButton(view, song.urlKey!!)
            }
        }
    }

    private fun setPlaySongFloatingMenuButton(view: View, urrlKey: String)
    {
        val playSongFloatingActionButton = view.findViewById<View>(R.id.play_song_floating_menu_button) as FloatingActionButton
        if (isPlayVideo(urrlKey))
        {
            playSongFloatingActionButton.visibility = View.VISIBLE
            playSongFloatingActionButton.setOnClickListener {
                showYouTube(urrlKey)
                if (floatingActionMenu!!.isExpanded)
                {
                    floatingActionMenu!!.collapse()
                }
            }
        }
    }


    private fun setPresentSongFloatingMenuButton(view: View)
    {
        val presentSongFloatingMenuButton = view.findViewById<View>(R.id.present_song_floating_menu_button) as FloatingActionButton
        presentSongFloatingMenuButton.visibility = View.VISIBLE
        presentSongFloatingMenuButton.setOnClickListener {
            if (floatingActionMenu!!.isExpanded)
            {
                floatingActionMenu!!.collapse()
            }
            if (presentationScreenService!!.presentation != null)
            {
                presentSelectedVerse(0)
                floatingActionMenu!!.visibility = View.GONE
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else
            {
                Toast.makeText(activity, "Your device is not connected to any remote display", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPresentSongFloatingButton(view: View)
    {
        presentSongFloatingButton = view.findViewById<View>(R.id.present_song_floating_button) as FloatingActionButton
        presentSongFloatingButton!!.visibility = View.VISIBLE
        presentSongFloatingButton!!.setOnClickListener {
            if (presentationScreenService!!.presentation != null)
            {
                presentSelectedVerse(0)
                presentSongFloatingButton!!.visibility = View.GONE
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else
            {
                Toast.makeText(activity, "Your device is not connected to any remote display", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPlaySongFloatingButton(view: View, urlKey: String)
    {
        val playSongFloatingButton = view.findViewById<View>(R.id.play_song_floating_button) as FloatingActionButton
        playSongFloatingButton.visibility = View.VISIBLE
        playSongFloatingButton.setOnClickListener { showYouTube(urlKey) }
    }

    private fun showYouTube(urlKey: String)
    {
        Log.i(this.javaClass.simpleName, "Url key: $urlKey")
        val youTubeIntent = Intent(activity, CustomYoutubeBoxActivity::class.java)
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey)
        youTubeIntent.putExtra(CommonConstants.TITLE_KEY, title)
        activity!!.startActivity(youTubeIntent)
    }


    private fun setNextButton(view: View)
    {
        nextButton = view.findViewById<View>(R.id.next_verse_floating_button) as FloatingActionButton
        nextButton!!.visibility = View.GONE
        nextButton!!.setOnClickListener(NextButtonOnClickListener())
    }

    private fun setPreviousButton(view: View)
    {
        previousButton = view.findViewById<View>(R.id.previous_verse_floating_button) as FloatingActionButton
        previousButton!!.visibility = View.GONE
        previousButton!!.setOnClickListener(PreviousButtonOnClickListener())
    }

    override fun fragmentBecameVisible()
    {
        onBecameVisible(song)
    }

    private fun onBecameVisible(song: Song?)
    {
        val presentingSong = Setting.instance.song
        if (presentingSong != null && presentingSong == song && presentationScreenService!!.presentation != null)
        {
            setPresentation(song)
        } else
        {
            hideOrShowComponents(song)
        }
        setActionBarTitle()
    }

    private fun setPresentation(song: Song)
    {
        val currentPosition = Setting.instance.slidePosition
        presentSelectedVerse(currentPosition)
        if (floatingActionMenu != null)
        {
            floatingActionMenu!!.visibility = View.GONE
        }
        if (presentSongFloatingButton != null)
        {
            presentSongFloatingButton!!.visibility = View.GONE
        }
        nextButton!!.visibility = if (song.contents!!.size - 1 == currentPosition) View.GONE else View.VISIBLE
        previousButton!!.visibility = if (currentPosition == 0) View.GONE else View.VISIBLE
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun hideOrShowComponents(song: Song?)
    {
        if (nextButton != null)
        {
            nextButton!!.visibility = View.GONE
        }
        if (previousButton != null)
        {
            previousButton!!.visibility = View.GONE
        }
        if (isPlayVideo(song!!.urlKey) && isPresentSong && floatingActionMenu != null)
        {
            floatingActionMenu!!.visibility = View.VISIBLE
        } else if (presentSongFloatingButton != null)
        {
            presentSongFloatingButton!!.visibility = View.VISIBLE
        }
        if (presentSongCardViewAdapter != null)
        {
            presentSongCardViewAdapter!!.setItemSelected(-1)
            presentSongCardViewAdapter!!.notifyDataSetChanged()
        }
        if (listView != null)
        {
            listView!!.smoothScrollToPosition(0)
        }
    }

    private inner class NextButtonOnClickListener : View.OnClickListener
    {

        override fun onClick(v: View)
        {
            val position = presentSongCardViewAdapter!!.selectedItem + 1
            listView!!.smoothScrollToPositionFromTop(position, 2)
            presentSelectedVerse(if (position <= song!!.contents!!.size) position else position - 1)
        }
    }

    private inner class PreviousButtonOnClickListener : View.OnClickListener
    {
        override fun onClick(v: View)
        {
            val position = presentSongCardViewAdapter!!.selectedItem - 1
            val previousPosition = if (position >= 0) position else 0
            listView!!.smoothScrollToPosition(previousPosition, 2)
            presentSelectedVerse(previousPosition)
        }
    }

    private inner class ListViewOnItemClickListener : AdapterView.OnItemClickListener
    {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
        {
            if (previousButton!!.visibility == View.VISIBLE || nextButton!!.visibility == View.VISIBLE)
            {
                listView!!.smoothScrollToPositionFromTop(position, 2)
                presentSelectedVerse(position)
            }
            if (floatingActionMenu != null && floatingActionMenu!!.isExpanded)
            {
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                floatingActionMenu!!.collapse()
                val color = 0x00000000
                setListViewForegroundColor(color)
            }
        }

    }

    private fun presentSelectedVerse(position: Int)
    {
        if (presentationScreenService!!.presentation != null)
        {
            presentationScreenService!!.showNextVerse(song, position)
            presentSongCardViewAdapter!!.setItemSelected(position)
            presentSongCardViewAdapter!!.notifyDataSetChanged()
            previousButton!!.visibility = if (position <= 0) View.GONE else View.VISIBLE
            nextButton!!.visibility = if (position >= song!!.contents!!.size - 1) View.GONE else View.VISIBLE
        }
    }

    private inner class ListViewOnItemLongClickListener : AdapterView.OnItemLongClickListener
    {

        internal val isCopySelectedVerse: Boolean
            get() = !isPresentSong || isPlayVideo(song!!.urlKey) && floatingActionMenu != null && floatingActionMenu!!.visibility == View.VISIBLE || presentSongFloatingButton != null && presentSongFloatingButton!!.visibility == View.VISIBLE

        override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean
        {
            if (isCopySelectedVerse)
            {
                val selectedVerse = song!!.contents!![position]
                presentSongCardViewAdapter!!.setItemSelected(position)
                presentSongCardViewAdapter!!.notifyDataSetChanged()
                shareSongInSocialMedia(selectedVerse)
            }
            return false
        }

        internal fun shareSongInSocialMedia(selectedText: String)
        {
            val formattedContent = song!!.title + "\n\n" + customTagColorService.getFormattedLines(selectedText) + "\n" + String.format(getString(R.string.verse_share_info), getString(R.string.app_name))
            val textShareIntent = Intent(Intent.ACTION_SEND)
            textShareIntent.putExtra(Intent.EXTRA_TEXT, formattedContent)
            textShareIntent.type = "text/plain"
            val intent = Intent.createChooser(textShareIntent, "Share verse with...")
            activity!!.startActivity(intent)
        }
    }

    private fun setListViewForegroundColor(color: Int)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            listView!!.foreground = ColorDrawable(color)
        }
    }

    private fun isPlayVideo(urrlKey: String?): Boolean
    {
        val playVideoStatus = preferenceSettingService.isPlayVideo
        return urrlKey != null && urrlKey.length > 0 && playVideoStatus
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        if (youTubePlayer != null)
        {
            outState.putInt(KEY_VIDEO_TIME, youTubePlayer.currentTimeMillis)
            Log.i(this.javaClass.simpleName, "Video duration: " + youTubePlayer.currentTimeMillis)
        }
    }

    private inner class SongContentPortraitViewTouchListener : View.OnTouchListener
    {
        override fun onTouch(v: View, event: MotionEvent): Boolean
        {
            val position = tilteList!!.indexOf(title)
            Setting.instance.position = position
            return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        menu.clear()
        if (CommonUtils.isPhone(context!!))
        {
            inflater!!.inflate(R.menu.action_bar_options, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        Log.i(SongContentPortraitViewFragment::class.java.simpleName, "Menu item " + item.itemId + " " + R.id.options)
        when (item.itemId)
        {
            android.R.id.home ->
            {
                activity!!.finish()
                return true
            }
            R.id.options ->
            {
                Log.i(SongContentPortraitViewFragment::class.java.simpleName, "On tapped options")
                popupMenuService = PopupMenuService()
                PermissionUtils.isStoragePermissionGranted(activity as Activity)
                popupMenuService!!.showPopupmenu(activity as AppCompatActivity, activity!!.findViewById(R.id.options), title!!, false)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu)
    {
        super.onPrepareOptionsMenu(menu)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
        {
            setActionBarTitle()
        }
    }

    private fun setActionBarTitle()
    {
        val appCompatActivity = activity as AppCompatActivity?
        try
        {
            if (preferenceSettingService != null && tilteList!!.size > 0 && CommonUtils.isPhone(context!!))
            {
                val title: String
                if (tilteList!!.size == 1)
                {
                    title = tilteList!![0]
                } else
                {
                    title = tilteList!![Setting.instance.position]
                }
                val song = songDao.findContentsByTitle(title)
                appCompatActivity!!.title = getTitle(song, title)
            }
        } catch (ex: Exception)
        {
            appCompatActivity!!.title = title
        }

    }

    private fun getTitle(song: Song?, defaultTitle: String): String
    {
        try
        {
            val title = if (preferenceSettingService.isTamil && song!!.tamilTitle!!.length > 0) song.tamilTitle
            else song!!.title
            return songBookNumber + title
        } catch (e: Exception)
        {
            return songBookNumber + defaultTitle
        }

    }

    companion object
    {
        val KEY_VIDEO_TIME = "KEY_VIDEO_TIME"


        fun newInstance(title: String, titles: ArrayList<String>): SongContentPortraitViewFragment
        {
            val songContentPortraitViewFragment = SongContentPortraitViewFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles)
            bundle.putString(CommonConstants.TITLE_KEY, title)
            songContentPortraitViewFragment.arguments = bundle
            return songContentPortraitViewFragment
        }

        fun newInstance(bundle: Bundle): SongContentPortraitViewFragment
        {
            val songContentPortraitViewFragment = SongContentPortraitViewFragment()
            songContentPortraitViewFragment.arguments = bundle
            return songContentPortraitViewFragment
        }
    }

}
