package org.worshipsongs.service;

import org.worshipsongs.domain.Author;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IAuthorService
{
    List<Author> findAll();

    String findNameByTitle(String title);
}
