package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.dao.AbstractDao;
import org.worshipsongs.dao.ITopicDao;
import org.worshipsongs.dao.TopicDao;
import org.worshipsongs.domain.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TopicsService implements ITopicsService
{
    private ITopicDao iTopicDao;

    public TopicsService(Context context)
    {
        iTopicDao = new TopicDao(context);
    }

    @Override
    public List<Topics> findAll()
    {
        return iTopicDao.findAll();
    }

    @Override
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
