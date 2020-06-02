package org.worshipsongs.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import kotlinx.android.synthetic.main.font_size_dialog.view.*
import org.worshipsongs.R

/**
 * @author Madasamy
 * @version 1.0.0
 */
class FontDialogPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs)
{
    private var fontSize: Int = 0
    private var defaultFontSize = 20
    private var maxSize = 100
    private val customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this@FontDialogPreference.context)

    init
    {
        layoutResource = R.layout.font_size_dialog
        isPersistent = true
        if (attrs != null)
        {
            maxSize = attrs.getAttributeIntValue(null, "maxSize", 100)
            defaultFontSize = attrs.getAttributeIntValue(null, "fontSize", 20)
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?)
    {
        super.onBindViewHolder(holder)
        with(holder!!.itemView){
           setFontSizeSeekBar(portrait_font_size, fontSize)
            setFontSizeTextView(fontSize)
            maxsize_textView.text = maxSize.toString()
        }
    }


    private fun setFontSizeSeekBar(fontSizeSeekBar: SeekBar, fontSizeTextView: TextView?)
    {
        fontSizeSeekBar.max = maxSize
        fontSize = customSharedPreference.getInt(key, defaultFontSize)
        fontSizeSeekBar.progress = fontSize
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {
                fontSize = progress
                val text = context.resources.getString(R.string.fontSize) + ": " + fontSize
                fontSizeTextView!!.text = text
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

    private fun setFontSizeTextView(fontSizeTextView: TextView?)
    {
        val text = context.resources.getString(R.string.fontSize) + ": " + fontSize
        fontSizeTextView!!.text = text
    }

    private fun saveFontSizePreference(key: String, fontSize: Int)
    {
        val fontSizePreference = PreferenceManager.getDefaultSharedPreferences(this@FontDialogPreference.context)
        val editor = fontSizePreference.edit()
        editor.putInt(key, fontSize)
        editor.apply()
    }
}
