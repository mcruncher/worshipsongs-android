package org.worshipsongs.dao;

import org.worshipsongs.domain.Author;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IAuthorDao
{
    List<Author> findAll();

    String findAuthorNameByTitle(String title);
}
