package org.worshipsongs.service;

import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ISongService
{

    List<Song> findAll();

    List<Song> filterSongs(String text, List<Song> songs);

    List<Song> findByAuthorId(int id);

    List<Song> findByTopicId(int id);

    String getTitle(boolean tamilLanguage,ServiceSong serviceSong);
}
