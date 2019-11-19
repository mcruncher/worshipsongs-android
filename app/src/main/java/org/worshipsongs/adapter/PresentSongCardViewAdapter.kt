package org.worshipsongs.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.service.CustomTagColorService
import org.worshipsongs.service.UserPreferenceSettingService

/**
 * Author : Madasamy
 * Version : x.x.x
 */

class PresentSongCardViewAdapter(context: Context, objects: List<String>) : ArrayAdapter<String>(context, R.layout.present_song_card_view, objects)
{
    var selectedItem = -1
    private val preferenceSettingService: UserPreferenceSettingService
    private val customTagColorService: CustomTagColorService

    init
    {
        preferenceSettingService = UserPreferenceSettingService()
        customTagColorService = CustomTagColorService()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {

        var view = convertView
        if (view == null)
        {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.present_song_card_view, null)
        }
        val verse = getItem(position)

        if (verse != null)
        {
            setTextView(position, view!!, verse)
        }
        return view!!
    }

    private fun setTextView(position: Int, v: View, verse: String)
    {
        val textView = v.findViewById<TextView>(R.id.verse_text_view)
        if (textView != null)
        {
            textView.text = ""
            customTagColorService.setCustomTagTextView(textView, verse, preferenceSettingService.primaryColor, preferenceSettingService.secondaryColor)
            textView.textSize = preferenceSettingService.portraitFontSize
            textView.setTextColor(preferenceSettingService.primaryColor)
        }
        if (selectedItem == position)
        {
            textView!!.setBackgroundResource(R.color.gray)
        } else
        {
            val typedValue = TypedValue()
            WorshipSongApplication.getContext().theme.resolveAttribute(android.R.attr.background, typedValue, true)
            textView!!.setBackgroundResource(typedValue.data)
        }
        textView.setLineSpacing(0f, 1.2f)
    }

    fun setItemSelected(position: Int)
    {
        selectedItem = position
    }
}


