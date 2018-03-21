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
    public static final String TABLE_NAME = "topics";
    private String[] allColumns = {"id", "name"};
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
        Cursor cursor = databaseService.getDatabase().query(true, TABLE_NAME,
                allColumns, null, null, null, null, allColumns[1], null);
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
