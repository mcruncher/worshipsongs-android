package org.worshipsongs.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.Song
import org.worshipsongs.service.CustomTagColorService
import org.worshipsongs.service.UserPreferenceSettingService

/**
 * author: Madasamy
 * version: 2.1.0
 */
class SongCardViewAdapter(private val song: Song, private val context: Context) : RecyclerView.Adapter<SongCardViewAdapter.SongContentViewHolder>()
{

    private var preferenceSettingService: UserPreferenceSettingService? = null
    private var customTagColorService: CustomTagColorService? = null

    override fun getItemCount(): Int
    {
        return song.contents.size
    }

    override fun onBindViewHolder(songContentViewHolder: SongContentViewHolder, position: Int)
    {
        customTagColorService = CustomTagColorService()
        preferenceSettingService = UserPreferenceSettingService()
        val verse = song.contents[position]
        songContentViewHolder.textView.text = verse
        loadTextStyle(songContentViewHolder.textView, position)
    }

    private fun loadTextStyle(textView: TextView, position: Int)
    {
        val text = textView.text.toString()
        textView.text = ""
        customTagColorService!!.setCustomTagTextView(textView, text, preferenceSettingService!!.primaryColor, preferenceSettingService!!.secondaryColor)
        textView.textSize = preferenceSettingService!!.portraitFontSize
        textView.setTextColor(preferenceSettingService!!.primaryColor)
        textView.isVerticalScrollBarEnabled = true
        val typedValue = TypedValue()
        WorshipSongApplication.getContext().theme.resolveAttribute(android.R.attr.background, typedValue, true)
        textView.setBackgroundResource(typedValue.data)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongContentViewHolder
    {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.song_content_view_card_layout, viewGroup, false)
        return SongContentViewHolder(itemView)
    }

    inner class SongContentViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var cardView: CardView
        var textView: TextView

        init
        {
            cardView = view.findViewById<View>(R.id.verse_card_view) as CardView
            textView = view.findViewById<View>(R.id.verse_text_view) as TextView
        }
    }
}
