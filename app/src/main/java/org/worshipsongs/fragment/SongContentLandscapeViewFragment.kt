package org.worshipsongs.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.service.CustomTagColorService
import org.worshipsongs.service.UserPreferenceSettingService

/**
 * author:madasamy
 * version:2.1.0
 */
class SongContentLandscapeViewFragment : Fragment()
{
    private var preferenceSettingService: UserPreferenceSettingService? = null
    private var customTagColorService: CustomTagColorService? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.song_content_landscape_view_fragment, container, false) as View
        hideStatusBar()
        customTagColorService = CustomTagColorService()
        preferenceSettingService = UserPreferenceSettingService()
        val bundle = arguments
        val title = bundle!!.getString(CommonConstants.TITLE_KEY)
        val content = bundle.getString("content")
        val authorName = bundle.getString("authorName")
        val position = bundle.getString("position")
        val size = bundle.getString("size")
        val chord = bundle.getString("chord")
        setScrollView(view)
        setContent(content, view)
        setSongTitle(view, title, chord)
        setAuthorName(view, authorName)
        setSongSlide(view, position, size)
        return view
    }

    private fun hideStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16)
        {
            activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else
        {
            val decorView = activity!!.window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }

    private fun setScrollView(view: View)
    {
        val scrollView = view.findViewById<View>(R.id.verse_land_scape_scrollview) as ScrollView
        scrollView.setBackgroundColor(preferenceSettingService!!.presentationBackgroundColor)
    }

    private fun setContent(content: String?, view: View)
    {
        val textView = view.findViewById<View>(R.id.text) as TextView
        textView.text = content
        val text = textView.text.toString()
        textView.text = ""
        customTagColorService!!.setCustomTagTextView(textView, text, preferenceSettingService!!.presentationPrimaryColor, preferenceSettingService!!.presentationSecondaryColor)
        textView.textSize = preferenceSettingService!!.landScapeFontSize
        textView.setTextColor(preferenceSettingService!!.primaryColor)
        textView.isVerticalScrollBarEnabled = true
    }

    private fun setSongTitle(view: View, title: String?, chord: String?)
    {
        val songTitleTextView = view.findViewById<View>(R.id.song_title) as TextView
        val formattedTitle = resources.getString(R.string.title) + " " + title + " " + getChord(chord)
        songTitleTextView.text = formattedTitle
        songTitleTextView.setTextColor(preferenceSettingService!!.presentationPrimaryColor)
    }

    private fun getChord(chord: String?): String
    {

        return if (chord != null && chord.length > 0)
        {
            " [$chord]"
        } else ""
    }

    private fun setAuthorName(view: View, authorName: String?)
    {
        val authorNameTextView = view.findViewById<View>(R.id.author_name) as TextView
        val formattedAuthor = resources.getString(R.string.author) + " " + authorName
        authorNameTextView.text = formattedAuthor
        authorNameTextView.setTextColor(preferenceSettingService!!.presentationPrimaryColor)
    }

    private fun setSongSlide(view: View, position: String?, size: String?)
    {
        val songSlideTextView = view.findViewById<View>(R.id.song_slide) as TextView
        val slidePosition = resources.getString(R.string.slide) + " " + getSongSlideValue(position, size)
        songSlideTextView.text = slidePosition
        songSlideTextView.setTextColor(preferenceSettingService!!.presentationPrimaryColor)
    }

    private fun getSongSlideValue(currentPosition: String?, size: String?): String
    {
        val slidePosition = Integer.parseInt(currentPosition!!) + 1
        return "$slidePosition of $size"
    }
}
