package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.worshipsongs.domain.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class TopicsService extends GenericService
{
    public static final String TABLE_NAME = "topics";
    private String[] allColumns = {"id", "name"};

    public TopicsService(Context context) {
        super(context);
    }

    public List<Topics> findAll()
    {
        List<Topics> authors = new ArrayList<Topics>();
        Cursor cursor = getDatabase().query(true, TABLE_NAME,
                allColumns, null, null, null, null, allColumns[1], null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Topics topics = cursorToTopics(cursor);
            authors.add(topics);
            cursor.moveToNext();
        }
        cursor.close();
        return authors;
    }

    private Topics cursorToTopics(Cursor cursor) {
        Topics topics = new Topics();
        topics.setId(cursor.getInt(0));
        topics.setName(cursor.getString(1));
        return topics;
    }
}
