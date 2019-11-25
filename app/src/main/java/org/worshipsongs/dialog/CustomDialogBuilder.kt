package org.worshipsongs.dialog

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView

import org.worshipsongs.domain.DialogConfiguration
import org.worshipsongs.R

import java.util.ArrayList

/**
 * Author : Madasamy
 * Version : 3.x
 */

class CustomDialogBuilder(private val context: Context, private val dialogConfiguration: DialogConfiguration)
{
    var editText: EditText? = null
    var builder: AlertDialog.Builder? = null
    private var listView: ListView? = null
    private var radioButtonAdapter: RadioButtonAdapter? = null

    init
    {
        setBuilder()
    }

    private fun setBuilder()
    {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.DialogTheme))
        builder!!.setView(getCustomView(context, dialogConfiguration))
        builder!!.setTitle(dialogConfiguration.title)
    }

    private fun getCustomView(context: Context, dialogConfiguration: DialogConfiguration): View
    {
        val li = LayoutInflater.from(context)
        val view = li.inflate(R.layout.custom_dialog, null)
        setMessage(view, dialogConfiguration)
        setEditText(view, dialogConfiguration)
        setListView(view, dialogConfiguration)
        return view
    }

    private fun setMessage(promptsView: View, dialogConfiguration: DialogConfiguration)
    {
        val messageTextView = promptsView.findViewById<View>(R.id.message) as TextView
        messageTextView.setText(Html.fromHtml(dialogConfiguration.message), TextView.BufferType.SPANNABLE)
        messageTextView.visibility = if (dialogConfiguration.message.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setEditText(promptsView: View, dialogConfiguration: DialogConfiguration)
    {
        editText = promptsView.findViewById<View>(R.id.name_edit_text) as EditText
        editText!!.visibility = if (dialogConfiguration.isEditTextVisibility) View.VISIBLE else View.GONE
    }

    private fun setListView(view: View, dialogConfiguration: DialogConfiguration)
    {
        listView = view.findViewById<View>(R.id.list_view) as ListView
        radioButtonAdapter = RadioButtonAdapter(context, ArrayList())
        listView!!.adapter = radioButtonAdapter
    }

    fun setSingleChoices(items: List<String>, checked: Int)
    {
        listView!!.visibility = View.VISIBLE
        listView!!.choiceMode = AbsListView.CHOICE_MODE_SINGLE
        radioButtonAdapter!!.clear()
        radioButtonAdapter!!.addAll(items)
        radioButtonAdapter!!.setSelectedItem(checked)
        radioButtonAdapter!!.notifyDataSetChanged()
    }

    fun setSingleChoiceOnClickListener(choiceOnClickListener: AdapterView.OnItemClickListener)
    {
        listView!!.onItemClickListener = choiceOnClickListener
    }

    private inner class RadioButtonAdapter internal constructor(context: Context, objects: List<String>) : ArrayAdapter<String>(context, R.layout.radiobutton_adapter, objects)
    {
        private var selectedItem = -1

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
        {
            var view = convertView
            if (view == null)
            {
                val layoutInflater = LayoutInflater.from(context)
                view = layoutInflater.inflate(R.layout.radiobutton_adapter, null)
            }
            setRadioButton(position, view!!)
            return view
        }

        private fun setRadioButton(position: Int, view: View)
        {
            val radioButton = view.findViewById<View>(R.id.radioButton) as RadioButton
            if (selectedItem == position)
            {
                radioButton.isChecked = true
            } else
            {
                radioButton.isChecked = false
            }
        }

        fun setSelectedItem(selectedItem: Int)
        {
            this.selectedItem = selectedItem
        }
    }
}
