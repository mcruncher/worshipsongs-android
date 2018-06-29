package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TopicService
{
    private DatabaseService databaseService;

    public TopicService()
    {
        //Invoke only in unit test
    }

    public TopicService(Context context)
    {
        databaseService = new DatabaseService(context);
    }

    public List<Topics> findAll()
    {
        List<Topics> topicsList = new ArrayList<Topics>();
        String query ="select  topic.id, topic.name, count(topic.id) from songs as song " +
                "inner join songs_topics as songtopics on songtopics.song_id=song.id " +
                "inner join topics as topic on topic.id=songtopics.topic_id group by name";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Topics topics = cursorToTopics(cursor);
            topicsList.add(topics);
            cursor.moveToNext();
        }
        cursor.close();
        return topicsList;
    }

    private Topics cursorToTopics(Cursor cursor)
    {
        Topics topics = new Topics();
        topics.setId(cursor.getInt(0));
        topics.setName(cursor.getString(1));
        topics.setNoOfSongs(cursor.getInt(2));
        topics.setTamilName(databaseService.parseTamilName(topics.getName()));
        topics.setDefaultName(databaseService.parseEnglishName(topics.getName()));
        return topics;
    }

    public List<Topics> filteredTopics(String query, List<Topics> topicsList)
    {
        List<Topics> filteredTextList = new ArrayList<Topics>();
        if (StringUtils.isBlank(query)) {
            filteredTextList.addAll(topicsList);
        } else {
            for (Topics topics : topicsList) {
                if (topics.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTextList.add(topics);
                }
            }
        }
        return filteredTextList;
    }
}
