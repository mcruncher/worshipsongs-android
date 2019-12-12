package org.worshipsongs.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.DragDrop
import org.worshipsongs.domain.SongDragDrop
import org.worshipsongs.service.UserPreferenceSettingService

import java.util.ArrayList

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

class FavouriteSongAdapter(songs: List<SongDragDrop>) : DragItemAdapter<SongDragDrop, FavouriteSongAdapter.ViewHolder>()
{
    private val userPreferenceSettingService = UserPreferenceSettingService()
    private var favouriteListener: FavouriteListener? = null

    init
    {
        setHasStableIds(true)
        itemList = songs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_song_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        super.onBindViewHolder(holder, position)
        val text = getTitle(mItemList[position])
        holder.mText.text = text
        holder.itemView.tag = mItemList[position]
    }

    private fun getTitle(songDragDrop: SongDragDrop): String
    {
        return if (userPreferenceSettingService.isTamil)
        {
            if (StringUtils.isNotBlank(songDragDrop.tamilTitle)) songDragDrop.tamilTitle!! else songDragDrop.title!!
        } else
        {
            songDragDrop.title!!
        }
    }

    override fun getItemId(position: Int): Long
    {
        return mItemList[position].id
    }

    fun setFavouriteListener(favouriteListener: FavouriteListener)
    {
        this.favouriteListener = favouriteListener
    }

     inner class ViewHolder(view: View) : DragItemAdapter.ViewHolder(view, R.id.image, false)
    {
        var mText: TextView
        var listSwipeItem: RelativeLayout

        init
        {
            mText = itemView.findViewById<View>(R.id.text) as TextView
            listSwipeItem = itemView.findViewById<View>(R.id.item_layout) as RelativeLayout
        }

        override fun onItemLongClicked(view: View?): Boolean
        {
            if (favouriteListener != null)
            {
                favouriteListener!!.onRemove(mItemList[adapterPosition])
            }
            return true
        }

        override fun onItemClicked(view: View?)
        {
            if (favouriteListener != null)
            {
                favouriteListener!!.onClick(mItemList[adapterPosition])
            }
        }
    }

    interface FavouriteListener
    {
        fun onRemove(dragDrop: SongDragDrop)

        fun onClick(dragDrop: SongDragDrop)
    }
}
