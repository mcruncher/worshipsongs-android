package org.worshipsongs.dao;

import org.worshipsongs.domain.SongBook;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public interface ISongBookDao
{


    List<SongBook> findAll();

}
