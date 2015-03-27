package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.SongBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 3/25/2015.
 */
public class SongBookDao extends AbstractDao {

    public static final String TABLE_NAME_AUTHOR = "song_books";
    public static final String COLUMN_ID = "id";
    public static final String SONG_BOOK_NAME = "name";
    public static final String SONG_BOOK_PUBLISHER = "publisher";
    private String[] allColumns = {COLUMN_ID, SONG_BOOK_NAME, SONG_BOOK_PUBLISHER};

    public SongBookDao(Context context)
    {
        super(context);
    }

    public List<SongBook> findAll()
    {
        List<SongBook> songBooks = new ArrayList<SongBook>();
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SongBook songBook = cursorToSong(cursor);
            songBooks.add(songBook);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return songBooks;
    }

    private SongBook cursorToSong(Cursor cursor)
    {
        SongBook songBook = new SongBook();
        songBook.setId(cursor.getInt(0));
        songBook.setName(cursor.getString(1));
        songBook.setPublisher(cursor.getString(2));
        return songBook;
    }
}
