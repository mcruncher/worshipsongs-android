package org.worshipsongs.parser

import android.content.Context
import android.util.Log
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.w3c.dom.CharacterData
import org.w3c.dom.Element
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.Verse
import org.worshipsongs.utils.RegexUtils
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @author: Madasamy
 * @version: 3.x
 */
class SongParser : ISongParser
{


    override fun parseContents(context: Context, lyrics: String, verseOrder: String): List<String>
    {
        Log.i(CLASS_NAME, "Verse order $verseOrder")
        val contents = ArrayList<String>()
        val verseList = parseVerse(WorshipSongApplication.context!!, lyrics)
        val verseDataMap = HashMap<String, String>()
        for (verses in verseList)
        {
            contents.add(verses.content!!)
            verseDataMap[verses.type!! + verses.label] = verses.content!!
        }
        val verseOrderList = getVerseOrders(verseOrder)
        if (!verseOrderList.isEmpty())
        {
            contents.clear()
            for (verseOrderKey in verseOrderList)
            {
                val content = verseDataMap[verseOrderKey]
                if (content != null)
                {
                    contents.add(content)
                }
            }
        }
        Log.d(this.javaClass.name, "Parsed contents :$contents")
        return contents
    }

    override fun parseVerse(context: Context, lyrics: String): List<Verse>
    {
        val verses = ArrayList<Verse>()
        var xmlFile: File? = null
        try
        {
            Log.i(CLASS_NAME, "Preparing to parse verse $lyrics")
            val externalCacheDir = context.externalCacheDir
            xmlFile = File.createTempFile(XML_FILE_NAME, EXTENSION, externalCacheDir)
            FileUtils.write(xmlFile!!, lyrics)
            val factory = DocumentBuilderFactory.newInstance()
            factory.isCoalescing = true
            val document = factory.newDocumentBuilder().parse(xmlFile)
            val nodeList = document.getElementsByTagName(VERSE_ELEMENT_NAME)
            for (i in 0 until nodeList.length)
            {
                val element = nodeList.item(i) as Element
                val verse = Verse()
                verse.label = Integer.parseInt(element.getAttribute(LABEL_ATTRIBUTE_NAME))
                verse.type = element.getAttribute(TYPE_ATTRIBUTE_NAME)
                verse.content = getCharacterDataFromElement(element)
                verses.add(verse)
            }
        } catch (ex: Exception)
        {
            Log.e(this.javaClass.name, "Error occurred while parsing verse", ex)
        } finally
        {
            FileUtils.deleteQuietly(xmlFile)
        }
        return verses
    }

    internal fun getCharacterDataFromElement(element: Element): String
    {
        val child = element.firstChild
        return if (child is CharacterData)
        {
            child.data
        } else ""
    }

    override fun parseMediaUrlKey(comments: String?): String
    {
        var mediaUrl = ""
        if (comments != null && comments.length > 0)
        {
            val mediaUrlLine = RegexUtils.getMatchString(comments, MEDIA_URL_REGEX)
            val medialUrlArray = mediaUrlLine.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (medialUrlArray != null && medialUrlArray.size >= 3)
            {
                mediaUrl = medialUrlArray[2]
            }
        }
        return mediaUrl
    }

    override fun parseChord(comments: String?): String
    {
        var chord = ""
        if (comments != null && comments.length > 0)
        {
            val chordLine = RegexUtils.getMatchString(comments, CHORD_REGEX)
            val chordArray = chordLine.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (chordArray != null && chordArray.size >= 2)
            {
                chord = chordArray[1]
            }
        }
        return chord
    }

    override fun parseTamilTitle(comments: String?): String
    {
        var tamilTitle = ""
        if (comments != null && comments.length > 0)
        {
            val tamilTitleLine = RegexUtils.getMatchString(comments, I_18_N_TITLE_REGEX)
            val chordArray = tamilTitleLine.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (chordArray != null && chordArray.size >= 2)
            {
                tamilTitle = chordArray[1]
            }
        }
        return tamilTitle
    }

    override fun getVerseOrders(verseOrder: String): List<String>
    {
        try
        {
            if (StringUtils.isNotBlank(verseOrder))
            {
                val verseArray = verseOrder.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Arrays.asList(*verseArray)
            }
        } catch (ex: Exception)
        {
            Log.i(CLASS_NAME, "Error ", ex)
        }

        return ArrayList()
    }

    companion object
    {

        private val CLASS_NAME = SongParser::class.java.simpleName

        val XML_FILE_NAME = "verse"
        val EXTENSION = "xml"
        val VERSE_ELEMENT_NAME = "verse"
        val LABEL_ATTRIBUTE_NAME = "label"
        val TYPE_ATTRIBUTE_NAME = "type"

        val I_18_N_TITLE_REGEX = "i18nTitle.*"
        val MEDIA_URL_REGEX = "mediaurl.*"
        val CHORD_REGEX = "originalKey.*"
    }
}
