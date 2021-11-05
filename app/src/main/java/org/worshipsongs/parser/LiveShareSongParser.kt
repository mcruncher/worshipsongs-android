package org.worshipsongs.parser

import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import org.worshipsongs.domain.Song
import java.io.File
import java.util.*

class LiveShareSongParser : ILiveShareSongParser
{
    override fun parseSong(serviceFilePath: String, songTitle: String): Song
    {
        var song = Song()
        val serviceDir = File(serviceFilePath)
        val readFileToString = FileUtils.readFileToString(serviceDir)
        var rootJsonArray = JSONArray(readFileToString)
        for (i in 0 until rootJsonArray.length())
        {
            val rootJsonObject = rootJsonArray.getJSONObject(i)
            val serviceItemObject = getJsonObject(rootJsonObject, SERVICE_ITEM_KEY);
            if (serviceItemObject != null)
            {
                val headerObject = serviceItemObject.getJSONObject(HEADER_KEY)
                val title = headerObject.getString(TITLE_KEY)
                if (songTitle.equals(title))
                {
                    val contents: MutableList<String> = ArrayList()
                    val data = serviceItemObject.getJSONArray(DATA_KEY)
                    for (i in 0 until data.length())
                    {
                        val dataObject = data.getJSONObject(i)
                        contents.add(dataObject.getString(RAW_SLIDE_KEY))
                    }
                    song.contents = contents
                    song.title = title
                    song.tamilTitle = title
                    song.authorName = headerObject.getJSONObject(DATA_KEY).getString(AUTHOR_KEY)
                    return song
                }
            }
        }
        return song
    }

    override fun parseTitles(serviceFilePath: String): MutableList<String>
    {
        var titles: MutableList<String> = ArrayList()
        val serviceDir = File(serviceFilePath)
        val readFileToString = FileUtils.readFileToString(serviceDir)
        var rootJsonArray = JSONArray(readFileToString)
        for (i in 0 until rootJsonArray.length())
        {
            val rootJsonObject = rootJsonArray.getJSONObject(i)
            val serviceItemObject = getJsonObject(rootJsonObject, SERVICE_ITEM_KEY);
            if (serviceItemObject != null)
            {
                val headerObject = serviceItemObject.getJSONObject(HEADER_KEY)
                val title = headerObject.getString(TITLE_KEY)
                titles.add(title)
            }
        }
        return titles
    }

    private fun getJsonObject(rootJsonObject: JSONObject, key: String): JSONObject?
    {
        try
        {
            return rootJsonObject.getJSONObject(key)
        } catch (e: Exception)
        {
            return null
        }
    }

    companion object
    {
        val SERVICE_ITEM_KEY = "serviceitem"
        val HEADER_KEY = "header"
        val TITLE_KEY = "title"
        val DATA_KEY = "data"
        val RAW_SLIDE_KEY = "raw_slide"
        val AUTHOR_KEY = "authors"
    }
}