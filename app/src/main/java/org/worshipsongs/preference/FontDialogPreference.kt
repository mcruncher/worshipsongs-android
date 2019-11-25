package org.worshipsongs.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView

import org.worshipsongs.R

/**
 * Author:Madasamy
 * version:1.0.0
 */
class FontDialogPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs)
{
    private var fontSize: Int = 0
    private var defaultFontSize = 20
    private var maxSize = 100
    private val customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this@FontDialogPreference.context)
    private var fontSizetextView: TextView? = null

    init
    {
        isPersistent = true
        if (attrs != null)
        {
            maxSize = attrs.getAttributeIntValue(null, "maxSize", 100)
            defaultFontSize = attrs.getAttributeIntValue(null, "fontSize", 20)
        }
    }

    override fun onCreateView(parent: ViewGroup): View
    {
        super.onCreateView(parent)
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.font_size_dialog, parent, false)
        setFontSizeSeekBar(view)
        setFontSizeTextView(view)
        setMaxSizeTextView(view)
        return view
    }

    private fun setFontSizeSeekBar(view: View)
    {
        val fontSizeSeekBar = view.findViewById<View>(R.id.portrait_font_size) as SeekBar
        fontSizeSeekBar.max = maxSize
        fontSize = customSharedPreference.getInt(key, defaultFontSize)
        fontSizeSeekBar.progress = fontSize
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {
                fontSize = progress
                val text = context.resources.getString(R.string.fontSize) + ": " + fontSize
                fontSizetextView!!.text = text
            }

            override fun onStartTrackingTouch(seekBar: SeekBar)
            {
                //Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar)
            {
                saveFontSizePreference(key, fontSize)
            }
        })
    }

    private fun setFontSizeTextView(view: View)
    {
        fontSizetextView = view.findViewById<View>(R.id.fontSize) as TextView
        val text = context.resources.getString(R.string.fontSize) + ": " + fontSize
        fontSizetextView!!.text = text
    }

    private fun setMaxSizeTextView(view: View)
    {
        val textView = view.findViewById<View>(R.id.maxsize_textView) as TextView
        textView.text = maxSize.toString()
    }

    private fun saveFontSizePreference(key: String, fontSize: Int)
    {
        val fontSizePreference = PreferenceManager.getDefaultSharedPreferences(this@FontDialogPreference.context)
        val editor = fontSizePreference.edit()
        editor.putInt(key, fontSize)
        editor.apply()
    }
}
