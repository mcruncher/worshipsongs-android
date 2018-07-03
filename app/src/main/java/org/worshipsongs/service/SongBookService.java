package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.SongBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class SongBookService
{
    public static final String TABLE_NAME = "song_books";
    public String[] allColumns = {"id", "name", "publisher"};
    private DatabaseService databaseService;
    private UserPreferenceSettingService userPreferenceSettingService;

    public SongBookService(Context context)
    {
        databaseService = new DatabaseService(context);
        userPreferenceSettingService = new UserPreferenceSettingService(context);
    }

    public List<SongBook> findAll()
    {
        List<SongBook> songBooks = new ArrayList<SongBook>();

        String query = "select songbook.id, songbook.name, songbook.publisher, count(songbook.id) " +
                "from songs as song inner join songs_songbooks as songsongbooks on " +
                "songsongbooks.song_id=song.id inner join song_books as songbook on " +
                "songbook.id=songsongbooks.songbook_id group by songbook.name";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SongBook songBook = cursorToSongBook(cursor);
            songBooks.add(songBook);
            cursor.moveToNext();
        }
        cursor.close();
        return songBooks;
    }

    private SongBook cursorToSongBook(Cursor cursor)
    {
        SongBook songBook = new SongBook();
        songBook.setId(cursor.getInt(0));
        songBook.setName(parseName(cursor.getString(1)));
        songBook.setPublisher(cursor.getString(2));
        songBook.setNoOfSongs(cursor.getInt(3));
        return songBook;
    }

    String parseName(String name)
    {
        if (userPreferenceSettingService.isTamil()) {
            return databaseService.parseTamilName(name);
        } else {
            return databaseService.parseEnglishName(name);
        }
    }

    public List<SongBook> filteredSongBooks(String query, List<SongBook> songBooks)
    {
        List<SongBook> filteredTextList = new ArrayList<SongBook>();
        if (StringUtils.isBlank(query)) {
            filteredTextList.addAll(songBooks);
        } else {
            for (SongBook songBook : songBooks) {
                if (songBook.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTextList.add(songBook);
                }
            }
        }
        return filteredTextList;
    }
}
