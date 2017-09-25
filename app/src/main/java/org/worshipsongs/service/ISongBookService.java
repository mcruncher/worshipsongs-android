package org.worshipsongs.service;

import org.worshipsongs.domain.SongBook;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public interface ISongBookService
{
    List<SongBook> findAll();

    List<SongBook> filteredSongBooks(String query, List<SongBook> songBooks);
}
