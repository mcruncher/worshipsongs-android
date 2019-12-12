package org.worshipsongs.dialog

import android.annotation.TargetApi
import android.app.Presentation
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView

import org.worshipsongs.R
import org.worshipsongs.service.CustomTagColorService
import org.worshipsongs.service.UserPreferenceSettingService

/**
 * Author : Madasamy
 * Version : 3.x
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class RemoteSongPresentation(context: Context, display: Display)//this.context = context;
    : Presentation(context, display)
{

    private val preferenceSettingService = UserPreferenceSettingService()
    private val customTagColorService = CustomTagColorService()

    // private Context context;
    private var songSlideTextView: TextView? = null
    private var imageView: ImageView? = null
    private var scrollView: ScrollView? = null
    private var verseTextView: TextView? = null
    private var songTitleTextView: TextView? = null
    private var authorNameTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_content_landscape_view_fragment)
        setImageView()
        setScrollView()
        setVerseView()
        setSongTitleView()
        setAuthorNameView()
        setSongSlide()
    }

    private fun setImageView()
    {
        imageView = findViewById<View>(R.id.logo_image_view) as ImageView
        setImageViewVisibility(View.VISIBLE)
    }

    fun setImageViewVisibility(visible: Int)
    {
        imageView!!.visibility = visible
        imageView!!.background = ColorDrawable(preferenceSettingService.presentationBackgroundColor)
    }

    private fun setScrollView()
    {
        scrollView = findViewById<View>(R.id.verse_land_scape_scrollview) as ScrollView
        setVerseVisibility(View.GONE)
    }

    fun setVerseVisibility(visible: Int)
    {
        scrollView!!.visibility = visible
        scrollView!!.background = ColorDrawable(preferenceSettingService.presentationBackgroundColor)
    }

    private fun setVerseView()
    {
        verseTextView = findViewById<View>(R.id.text) as TextView
        verseTextView!!.text = ""
    }

    fun setVerse(verse: String)
    {
        verseTextView!!.text = ""
        customTagColorService.setCustomTagTextView(verseTextView!!, verse, preferenceSettingService.presentationPrimaryColor, preferenceSettingService.presentationSecondaryColor)
        verseTextView!!.textSize = preferenceSettingService.landScapeFontSize
        // verseTextView.setTextColor(preferenceSettingService.getPrimaryColor());
        verseTextView!!.isVerticalScrollBarEnabled = true
    }

    private fun setSongTitleView()
    {
        songTitleTextView = findViewById<View>(R.id.song_title) as TextView
    }

    fun setSongTitleAndChord(title: String, chord: String, color: Int)
    {
        songTitleTextView!!.text = ""
        val formattedTitle = resources.getString(R.string.title) + " " + title + " " + getChord(chord)
        songTitleTextView!!.text = formattedTitle
        songTitleTextView!!.setTextColor(color)
    }

    private fun getChord(chord: String?): String
    {

        return if (chord != null && chord.length > 0)
        {
            " [$chord]"
        } else ""
    }

    private fun setAuthorNameView()
    {
        authorNameTextView = findViewById<View>(R.id.author_name) as TextView
    }

    fun setAuthorName(authorName: String, color: Int)
    {
        authorNameTextView!!.text = ""
        val formattedAuthor = resources.getString(R.string.author) + " " + authorName
        authorNameTextView!!.text = formattedAuthor
        authorNameTextView!!.setTextColor(color)
    }

    private fun setSongSlide()
    {
        songSlideTextView = findViewById<View>(R.id.song_slide) as TextView
    }

    fun setSlidePosition(position: Int, size: Int, color: Int)
    {
        songSlideTextView!!.text = ""
        val slidePosition = resources.getString(R.string.slide) + " " + getSongSlideValue(position, size)
        songSlideTextView!!.text = slidePosition
        songSlideTextView!!.setTextColor(color)
    }

    private fun getSongSlideValue(currentPosition: Int, size: Int): String
    {
        val slidePosition = currentPosition + 1
        return "$slidePosition of $size"
    }

    override fun onDisplayRemoved()
    {
        super.onDisplayRemoved()
        Log.i(RemoteSongPresentation::class.java.simpleName, "When display is removed")
    }
}
