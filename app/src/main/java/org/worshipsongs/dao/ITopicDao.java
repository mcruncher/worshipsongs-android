package org.worshipsongs.dao;

import org.worshipsongs.domain.Topics;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ITopicDao
{
    List<Topics> findAll();
}
