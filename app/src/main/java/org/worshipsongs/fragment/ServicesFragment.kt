package org.worshipsongs.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.FavouriteSongsActivity
import org.worshipsongs.adapter.TitleAdapter
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment
import org.worshipsongs.service.FavouriteService
import org.worshipsongs.service.PopupMenuService
import org.worshipsongs.utils.CommonUtils

import java.util.ArrayList

/**
 * @author : Madasamy
 * @since : 3.x
 */

class ServicesFragment : Fragment(), TitleAdapter.TitleAdapterListener<String>, AlertDialogFragment.DialogListener, ITabFragment
{
    private val favouriteService = FavouriteService()
    private var services: MutableList<String> = ArrayList()
    private var state: Parcelable? = null
    private var serviceListView: ListView? = null
    private var titleAdapter: TitleAdapter<String>? = null
    private var infoTextView: TextView? = null
    private val popupMenuService = PopupMenuService()

    private val negativeButtonListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY)
        }
        setHasOptionsMenu(true)
        initSetUp()
    }

    private fun initSetUp()
    {
        services = favouriteService.findNames()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
        setInfoTextView(view)
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

    private fun setListView(view: View)
    {
        serviceListView = view.findViewById<View>(R.id.song_list_view) as ListView
        titleAdapter = TitleAdapter((activity as AppCompatActivity?)!!, R.layout.songs_layout)
        titleAdapter!!.setTitleAdapterListener(this)
        titleAdapter!!.addObjects(services)
        serviceListView!!.adapter = titleAdapter
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        if (this.isAdded)
        {
            outState.putParcelable(CommonConstants.STATE_KEY, serviceListView!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume()
    {
        super.onResume()
        initSetUp()
        refreshListView()
    }

    //Adapter listener methods
   override fun setViews(objects: Map<String, Any>, text: String?)
    {
        val titleTextView = objects[CommonConstants.TITLE_KEY] as TextView?
        titleTextView!!.text = text!!
        titleTextView.setOnLongClickListener(TextViewLongClickListener(text))
        titleTextView.setOnClickListener(TextViewOnClickListener(text))

        val optionsImageView = objects[CommonConstants.OPTIONS_IMAGE_KEY] as ImageView?
        optionsImageView!!.visibility = View.VISIBLE
        optionsImageView.setOnClickListener { view -> popupMenuService.shareFavouritesInSocialMedia(activity as Activity, view, text) }
    }


    //Dialog Listener method
    override fun onClickPositiveButton(bundle: Bundle?, tag: String?)
    {
        val favouriteName = bundle!!.getString(CommonConstants.NAME_KEY, "")
        favouriteService.remove(favouriteName)
        services.clear()
        initSetUp()
        titleAdapter!!.addObjects(services)
        infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onClickNegativeButton()
    {
        // Do nothing
    }

    //Tab choices and reorder methods
    override fun defaultSortOrder(): Int
    {
        return 4
    }

    override val title: String get() { return "playlists"}


    override fun checked(): Boolean
    {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
    {

    }

    private inner class TextViewLongClickListener internal constructor(private val serviceName: String) : View.OnLongClickListener
    {

        private val positiveButtonListener: DialogInterface.OnClickListener
            get() = DialogInterface.OnClickListener { dialog, which ->
                favouriteService.remove(serviceName)
                services.clear()
                initSetUp()
                titleAdapter!!.addObjects(services)
                infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
            }

        override fun onLongClick(v: View): Boolean
        {
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle(getString(R.string.delete))
            alertDialogBuilder.setMessage(getString(R.string.message_delete_playlist, serviceName))
            alertDialogBuilder.setPositiveButton(getString(R.string.ok), positiveButtonListener)
            alertDialogBuilder.setNegativeButton(R.string.cancel, negativeButtonListener)
            alertDialogBuilder.show()
            return false
        }
    }

    private inner class TextViewOnClickListener internal constructor(private val serviceName: String) : View.OnClickListener
    {

        override fun onClick(v: View)
        {
            val intent = Intent(activity, FavouriteSongsActivity::class.java)
            intent.putExtra(CommonConstants.SERVICE_NAME_KEY, serviceName)
            startActivity(intent)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
        {
            hideKeyboard()
            initSetUp()
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

    private fun refreshListView()
    {
        if (state != null)
        {
            serviceListView!!.onRestoreInstanceState(state)
        } else if (titleAdapter != null)
        {
            titleAdapter!!.addObjects(services)
            infoTextView!!.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    companion object
    {

        fun newInstance(): ServicesFragment
        {
            return ServicesFragment()
        }
    }
}
