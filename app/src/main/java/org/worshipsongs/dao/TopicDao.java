package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Topics;
import org.worshipsongs.utils.RegexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TopicDao extends AbstractDao implements ITopicDao
{
    public static final String TABLE_NAME = "topics";
    private String[] allColumns = {"id", "name"};
    public static final String TOPIC_NAME_REGEX = "\\{.*\\}";

    public TopicDao()
    {
        //Invoke only in unit test
    }

    public TopicDao(Context context)
    {
        super(context);
    }

    @Override
    public List<Topics> findAll()
    {
        List<Topics> topicsList = new ArrayList<Topics>();
        Cursor cursor = getDatabase().query(true, TABLE_NAME,
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
        topics.setTamilName(parseTamilTopicName(topics.getName()));
        topics.setDefaultName(parseDefaultName(topics.getName()));
        return topics;
    }

    String parseTamilTopicName(String topicName)
    {
        if (StringUtils.isNotBlank(topicName)) {
            String tamilTopicName = RegexUtils.getMatchString(topicName, TOPIC_NAME_REGEX);
            String formattedTopicName = tamilTopicName.replaceAll("\\{", "").replaceAll("\\}", "");
            return StringUtils.isNotBlank(formattedTopicName) ? formattedTopicName : topicName;
        }
        return "";
    }

    String parseDefaultName(String topicName)
    {
        if (StringUtils.isNotBlank(topicName)) {
            return topicName.replaceAll(TOPIC_NAME_REGEX, "");
        }
        return "";
    }
}
