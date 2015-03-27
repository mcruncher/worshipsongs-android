package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.worshipsongs.domain.AuthorSong;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorSongDao extends AbstractDao {

    public static final String TABLE_NAME_AUTHOR = "authors_songs";
    public static final String AUTHOR_ID = "author_id";
    public static final String SONG_ID = "song_id";
    private String[] allColumns = {AUTHOR_ID, SONG_ID};

    public AuthorSongDao(Context context)
    {
        super(context);
    }

    public List<AuthorSong> findAll()
    {
        List<AuthorSong> authors = new ArrayList<AuthorSong>();
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AuthorSong authorSong = cursorToSong(cursor);
            authors.add(authorSong);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return authors;
    }

    public List<AuthorSong> findSongId(int authorId)
    {
        List<AuthorSong> authors = new ArrayList<AuthorSong>();
        String whereClause = " author_id" + "="+authorId +";";
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                allColumns, whereClause, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AuthorSong authorSong = cursorToSong(cursor);
            authors.add(authorSong);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        Log.d(this.getClass().getName(), "Author songs dao count" + authors.size());
        return authors;
    }


    private AuthorSong cursorToSong(Cursor cursor)
    {
        AuthorSong authorSong = new AuthorSong();
        authorSong.setAuthorId(cursor.getInt(0));
        authorSong.setSongId(cursor.getInt(1));
        return authorSong;
    }
}
