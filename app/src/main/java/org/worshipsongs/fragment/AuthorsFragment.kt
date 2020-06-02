package org.worshipsongs.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.SongListActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.domain.Author
import org.worshipsongs.domain.Type
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.service.AuthorService
import org.worshipsongs.service.UserPreferenceSettingService
import org.worshipsongs.utils.CommonUtils
import java.util.*


/**
 * @author: Madasamy
 * @since: 3.x
 */
class AuthorsFragment : AbstractTabFragment(), TitleAdapter.TitleAdapterListener<Author>, ITabFragment
{

    private var state: Parcelable? = null
    private var authorService: AuthorService? = null
    private val authorList = ArrayList<Author>()
    private var authorListView: ListView? = null
    private var titleAdapter: TitleAdapter<Author>? = null
    private val userPreferenceSettingService = UserPreferenceSettingService()

    override val title: String
        get()
        {
            return "artists"
        }

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
        authorService = AuthorService(context!!)
        for (author in authorService!!.findAll())
        {
            if (!author.name!!.toLowerCase().contains("unknown") && author.name != null)
            {
                authorList.add(author)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false) as View
        setListView(view)
        return view
    }

    private fun setListView(view: View)
    {
        authorListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(authorService!!.getAuthors("", authorList))
        authorListView!!.adapter = titleAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.action_search)
        val image = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        val drawable = image.drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                titleAdapter!!.addObjects(authorService!!.getAuthors(query, authorList))
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean
            {
                titleAdapter!!.addObjects(authorService!!.getAuthors(newText, authorList))
                return true

            }
        })
        menu.getItem(0).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu)
    {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return super.onOptionsItemSelected(item)
    }


    override fun onSaveInstanceState(outState: Bundle)
    {
        if (this.isAdded)
        {
            outState.putParcelable(STATE_KEY, authorListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        if (state != null)
        {
            authorListView!!.onRestoreInstanceState(state)
        } else
        {
            titleAdapter!!.addObjects(authorService!!.getAuthors("", authorList))
        }
    }

    override fun onPause()
    {
        state = authorListView!!.onSaveInstanceState()
        super.onPause()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
        {
            CommonUtils.hideKeyboard(activity)
        }
    }

    override fun setViews(objects: Map<String, Any>, author: Author?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = getAuthorName(author!!)
        titleTextView.setOnClickListener(textViewOnClickListener(author))
        val countTextView = objects[CommonConstants.SUBTITLE_KEY] as TextView?
        setCountView(countTextView!!, author.noOfSongs.toString())
    }

    private fun textViewOnClickListener(author: Author): View.OnClickListener
    {
        return View.OnClickListener {
            val intent = Intent(context, SongListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(CommonConstants.TYPE, Type.AUTHOR.name)
            intent.putExtra(CommonConstants.TITLE_KEY, getAuthorName(author))
            intent.putExtra(CommonConstants.ID, author.id)
            startActivity(intent)
        }
    }

    private fun getAuthorName(author: Author): String
    {
        return if (userPreferenceSettingService.isTamil) author.tamilName!! else author.defaultName!!
    }


    override fun defaultSortOrder(): Int
    {
        return 1
    }


    override fun checked(): Boolean
    {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
    {
        //DO nothing
    }

    companion object
    {
        private val STATE_KEY = "listViewState"

        fun newInstance(): AuthorsFragment
        {
            return AuthorsFragment()
        }
    }


}
