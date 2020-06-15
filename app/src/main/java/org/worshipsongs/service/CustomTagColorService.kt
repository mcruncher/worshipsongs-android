package org.worshipsongs.service

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi

import org.worshipsongs.utils.PropertyUtils

import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Madasamy
 * @since 1.1.0
 */
open class CustomTagColorService
{
    private val preferenceSettingService = UserPreferenceSettingService()
    private var tagExists: Boolean? = false

    fun setCustomTagTextView(textView: TextView, text: String, primaryColor: Int, secondaryColor: Int)
    {
        tagExists = pattern.matcher(text).find()
        val strings = getStringsByTag(text)
        var tagKey: String? = null

        for (i in strings.indices)
        {
            val matcher = pattern.matcher(strings[i])
            if (matcher.find())
            {
                val value = matcher.group(0).replace("{", "")
                tagKey = value.replace("}", "")
                if (displayTamilLyrics() || !displayRomanisedLyrics())
                {
                    setColoredTextView(textView, removeTag(strings[i], tagKey), secondaryColor)
                }
            } else
            {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || (!tagExists!!))
                {
                    setColoredTextView(textView, strings[i], primaryColor)
                }
            }
        }
    }

    protected open fun setColoredTextView(textView: TextView, content: String, color: Int)
    {
        PropertyUtils.appendColoredText(textView, content, color)
    }

    protected open fun displayTamilLyrics(): Boolean
    {
        return preferenceSettingService.isTamilLyrics
    }

    protected open fun displayRomanisedLyrics(): Boolean
    {
        return preferenceSettingService.isRomanisedLyrics
    }

    fun getFormattedLines(content: String): String
    {
        tagExists = pattern.matcher(content).find()
        val lyricsBuilder = StringBuilder()
        val lyricsListWithTag = getStringsByTag(content)
        var tagKey: String? = null
        for (i in lyricsListWithTag.indices)
        {
            val matcher = pattern.matcher(lyricsListWithTag[i])
            if (matcher.find())
            {
                if (displayTamilLyrics() || !displayRomanisedLyrics())
                {
                    val value = matcher.group(0).replace("{", "")
                    tagKey = value.replace("}", "")
                    lyricsBuilder.append(removeTag(lyricsListWithTag[i], tagKey))
                    lyricsBuilder.append("\n")
                }
            } else
            {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || (!tagExists!!))
                {
                    lyricsBuilder.append(lyricsListWithTag[i])
                    lyricsBuilder.append("\n")
                }
            }
        }
        return lyricsBuilder.toString()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getFormattedPage(content: String, page: PdfDocument.Page, xPos: Float, yPos: Float): Float
    {
        var yPos = yPos
        tagExists = pattern.matcher(content).find()
        val lyricsListWithTag = getStringsByTag(content)
        var tagKey: String? = null
        for (i in lyricsListWithTag.indices)
        {
            val matcher = pattern.matcher(lyricsListWithTag[i])
            if (matcher.find())
            {
                if (displayTamilLyrics() || !displayRomanisedLyrics())
                {
                    val value = matcher.group(0).replace("{", "")
                    tagKey = value.replace("}", "")
                    page.canvas.drawText(removeTag(lyricsListWithTag[i], tagKey) + "\n", xPos, yPos, Paint())
                    yPos = yPos + 20
                }
            } else
            {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || (!tagExists!!))
                {
                    page.canvas.drawText(lyricsListWithTag[i] + "\n", xPos, yPos, Paint())
                    yPos = yPos + 20
                }
            }
        }
        return yPos
    }

    private fun getStringsByTag(songContent: String): List<String>
    {
        val strings = ArrayList<String>()
        val split = songContent.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var startMatcher: Matcher? = null
        val endPattern = Pattern.compile("\\{/\\w\\}")
        var endMatcher: Matcher? = null
        var string: String?
        var i = 0
        while (i < split.size)
        {
            string = split[i]
            startMatcher = pattern.matcher(string)
            val builder = StringBuilder()
            if (startMatcher!!.find())
            {
                var j = i
                var endMatcherExists = false
                do
                {
                    j = j + 1
                    builder.append(string)
                    //System.out.println("String 1" + string);
                    endMatcher = endPattern.matcher(string!!)
                    if (endMatcher!!.find())
                    {
                        endMatcherExists = true
                    } else
                    {
                        if (j < split.size)
                        {
                            string = split[j]
                        } else
                        {
                            string = null
                        }
                        i = j
                    }
                    if (!endMatcherExists && string != null)
                    {
                        endMatcher = endPattern.matcher(string)
                        if (endMatcher!!.find())
                        {
                            endMatcherExists = true
                            builder.append(string)
                        }
                    }
                } while (!endMatcherExists && string != null)
                strings.add(builder.toString())
            } else
            {
                strings.add(string)
            }
            i++
        }
        return strings
    }

    fun removeTag(line: String, tagKey: String): String
    {
        val firstRemovePattern = "\\{$tagKey\\}"
        val secondRemovePattern = "\\{/$tagKey\\}"
        val replacedWithFirstPattern = line.replace(firstRemovePattern.toRegex(), "")
        return replacedWithFirstPattern.replace(secondRemovePattern.toRegex(), "")
    }

    companion object
    {
        private val pattern = Pattern.compile("\\{\\w\\}")
    }

}
