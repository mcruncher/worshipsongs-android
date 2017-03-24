package org.worshipsongs.service;

import org.worshipsongs.domain.Topics;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ITopicsService
{
    List<Topics> findAll();

    List<Topics> filteredTopics(String query, List<Topics> topicsList);
}
