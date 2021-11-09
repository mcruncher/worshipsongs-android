package org.worshipsongs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.woxthebox.draglistview.DragItemAdapter

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.R
import org.worshipsongs.domain.SongDragDrop
import org.worshipsongs.service.UserPreferenceSettingService

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
        holder.titleText.text = text
        holder.songBookNameText.text = getSongBookName(mItemList[position])
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

    private fun getSongBookName(songDragDrop: SongDragDrop): String
    {
        if (userPreferenceSettingService.isTamil && StringUtils.isNotBlank(songDragDrop.tamilSongBookName)) {
            return songDragDrop.tamilSongBookName!!
        }
        else if (StringUtils.isNotBlank(songDragDrop.songBookName)) {
            return songDragDrop.songBookName!!
        }
        return ""
    }

    override fun getUniqueItemId(position: Int): Long
    {
        return mItemList[position].id
    }

    fun setFavouriteListener(favouriteListener: FavouriteListener)
    {
        this.favouriteListener = favouriteListener
    }

    inner class ViewHolder(view: View) : DragItemAdapter.ViewHolder(view, R.id.image, false)
    {
        var titleText: TextView
        var songBookNameText: TextView
        var listSwipeItem: RelativeLayout

        init
        {
            titleText = itemView.findViewById<View>(R.id.text) as TextView
            songBookNameText = itemView.findViewById<View>(R.id.songBookName_text_view) as TextView
            songBookNameText.visibility = if (userPreferenceSettingService.displaySongBook) View.VISIBLE else View.GONE
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
