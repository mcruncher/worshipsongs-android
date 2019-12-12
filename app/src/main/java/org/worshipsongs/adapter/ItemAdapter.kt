package org.worshipsongs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast

import com.woxthebox.draglistview.DragItemAdapter

import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.DragDrop

import java.util.ArrayList

/**
 * author:  Madasamy
 * version: 3.x.x
 */

class ItemAdapter(list: ArrayList<DragDrop>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<DragDrop, ItemAdapter.ViewHolder>()
{
    private var listener: Listener? = null

    init
    {
        setHasStableIds(true)
        itemList = list
        if (listener != null)
        {
            listener!!.enableButton(isEnable(list))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        super.onBindViewHolder(holder, position)
        val text = mItemList[position].title
        val identifier = WorshipSongApplication.context!!.resources.getIdentifier(text, "string", WorshipSongApplication.context!!.packageName)
        holder.mText.text = WorshipSongApplication.context!!.getString(identifier)
        holder.checkbox.isChecked = mItemList[position].isChecked
        holder.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            mItemList[position].isChecked = isChecked
            if (listener != null)
            {
                listener!!.enableButton(isEnable(mItemList))
            }
        }
        holder.itemView.tag = mItemList[position]
    }

    override fun getItemId(position: Int): Long
    {
        return mItemList[position].id
    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress)
    {
        var mText: TextView
        var checkbox: CheckBox

        init
        {
            mText = itemView.findViewById<View>(R.id.text) as TextView
            checkbox = itemView.findViewById<View>(R.id.checkBox) as CheckBox
        }

    }

    private fun isEnable(dragDrops: List<DragDrop>): Boolean
    {
        for (dragDrop in dragDrops)
        {
            if (dragDrop.isChecked)
            {
                return true
            }
        }
        return false
    }

    fun setListener(listener: Listener)
    {
        this.listener = listener
    }

    interface Listener
    {

        fun enableButton(enable: Boolean)
    }
}
