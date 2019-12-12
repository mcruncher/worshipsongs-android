package org.worshipsongs.preference

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView

import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragListView

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.adapter.ItemAdapter
import org.worshipsongs.domain.DragDrop
import org.worshipsongs.registry.FragmentRegistry

import java.util.ArrayList

/**
 * Author : Madasamy
 * Version : 3.x
 */

class TabChoicePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ItemAdapter.Listener
{
    private var configureDragDrops: ArrayList<DragDrop>? = null
    private var mDragListView: DragListView? = null
    private var defaultSharedPreferences: SharedPreferences? = null
    private val fragmentRegistry = FragmentRegistry()

    init
    {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        setItems()
        dialogLayoutResource = R.layout.tab_choice_layout
    }

    override fun onBindDialogView(view: View)
    {
        view.setBackgroundColor(context.resources.getColor(R.color.white))
        setDragListView(view)
        super.onBindDialogView(view)
    }

    override fun onDialogClosed(positiveResult: Boolean)
    {
        Log.i(TabChoicePreference::class.java.simpleName, "Tab choice preference $positiveResult")
        if (positiveResult)
        {
            val itemList = mDragListView!!.adapter.itemList as ArrayList<DragDrop>
            defaultSharedPreferences!!.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(itemList)).apply()
            defaultSharedPreferences!!.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply()
        }
        super.onDialogClosed(positiveResult)
    }

    private fun setItems()
    {
        configureDragDrops = DragDrop.toArrays(defaultSharedPreferences!!.getString(CommonConstants.TAB_CHOICE_KEY, "")!!)
        val defaultList = fragmentRegistry.getDragDrops(context as Activity)
        if (configureDragDrops == null || configureDragDrops!!.isEmpty())
        {
            configureDragDrops = defaultList
            defaultSharedPreferences!!.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(configureDragDrops!!)).apply()
        } else if (defaultList.size > configureDragDrops!!.size)
        {
            defaultList.removeAll(configureDragDrops!!)
            configureDragDrops!!.addAll(defaultList)
        }
    }

    private fun setDragListView(view: View)
    {
        mDragListView = view.findViewById<View>(R.id.drag_list_view) as DragListView
        mDragListView!!.recyclerView.isVerticalScrollBarEnabled = true
        mDragListView!!.setLayoutManager(LinearLayoutManager(context))
        val listAdapter = ItemAdapter(configureDragDrops!!, R.layout.tab_choice_item, R.id.image, false)
        listAdapter.setListener(this)
        mDragListView!!.setAdapter(listAdapter, true)
        mDragListView!!.setCanDragHorizontally(false)
        mDragListView!!.setCustomDragItem(MyDragItem(context, R.layout.tab_choice_item))
    }

    override fun enableButton(enable: Boolean)
    {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = enable
    }

    private class MyDragItem internal constructor(context: Context, layoutId: Int) : DragItem(context, layoutId)
    {

        override fun onBindDragView(clickedView: View, dragView: View)
        {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            (dragView.findViewById<View>(R.id.text) as TextView).text = text
            dragView.findViewById<View>(R.id.item_layout).setBackgroundColor(dragView.resources.getColor(R.color.list_item_background))
        }
    }

}
