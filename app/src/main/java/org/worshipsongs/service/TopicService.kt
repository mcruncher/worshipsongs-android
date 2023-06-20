package org.worshipsongs.service

import android.content.Context
import android.database.Cursor
import android.util.Log
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.domain.Topics

/**
 * Author : Madasamy
 * Version : 3.x
 */

class TopicService
{
    private var databaseService: DatabaseService? = null

    constructor()
    {
        //Invoke only in unit test
    }

    constructor(context: Context)
    {
        databaseService = DatabaseService(context)
    }

    fun findAll(): List<Topics> {
        Log.d(TAG, "Finding all the topics...")
        val topicsList = ArrayList<Topics>()
        val query = "select  topic.id, topic.name, count(topic.id) from songs as song " + "inner join songs_topics as songtopics on songtopics.song_id=song.id " + "inner join topics as topic on topic.id=songtopics.topic_id group by name"
        try {
            val cursor = databaseService!!.database!!.rawQuery(query, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val topics = cursorToTopics(cursor)
                topicsList.add(topics)
                cursor.moveToNext()
            }
            cursor.close()
        } finally {
            databaseService!!.close()
        }
        return topicsList
    }

    private fun cursorToTopics(cursor: Cursor): Topics
    {
        val topics = Topics()
        topics.id = cursor.getInt(0)
        topics.name = cursor.getString(1)
        topics.noOfSongs = cursor.getInt(2)
        topics.tamilName = databaseService!!.parseTamilName(topics.name!!)
        topics.defaultName = databaseService!!.parseEnglishName(topics.name!!)
        return topics
    }

    fun filteredTopics(query: String, topicsList: List<Topics>): List<Topics>
    {
        val filteredTextList = ArrayList<Topics>()
        if (StringUtils.isBlank(query))
        {
            filteredTextList.addAll(topicsList)
        } else
        {
            for (topics in topicsList) {
                if (topics.name!!.toLowerCase().contains(query.toLowerCase())) {
                    filteredTextList.add(topics)
                }
            }
        }
        return filteredTextList
    }

    companion object {
        val TAG = TopicService::class.simpleName
    }
}
