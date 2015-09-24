package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorSongDao extends AbstractDao {

    public static final String TABLE_NAME_AUTHOR = "authors_songs";
    public static final String AUTHOR_ID = "author_id";
    private String[] columns = {AUTHOR_ID};
    public static final String SONG_ID = "song_id";
    private String[] allColumns = {AUTHOR_ID, SONG_ID};


    public AuthorSongDao(Context context)
    {
        super(context);
    }

    public AuthorSong findByTitle(String title)
    {
        String query = "select title, lyrics, verse_order, first_name, last_name, display_name" +
                " from songs as song , authors as author, authors_songs as authorsong where " +
                "song.id = authorsong.song_id and author.id = authorsong.author_id and song.title = ?";
        Cursor cursor = getDatabase().rawQuery(query, new String[]{title});
        cursor.moveToFirst();
        return getAuthorSong(cursor);

    }

    AuthorSong getAuthorSong(Cursor cursor)
    {
        Song song = new Song();
        song.setTitle(cursor.getString(0));
        song.setLyrics(cursor.getString(1));
        song.setVerseOrder(cursor.getString(2));

        Author author = new Author();
        author.setFirstName(cursor.getString(3));
        author.setLastName(cursor.getString(4));
        author.setDisplayName(cursor.getString(5));

        AuthorSong authorSong = new AuthorSong();
        authorSong.setSong(song);
        authorSong.setAuthor(author);
        return authorSong;
    }

    public List<AuthorSong> findAll()
    {
        List<AuthorSong> authors = new ArrayList<AuthorSong>();
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AuthorSong authorSong = cursorToAuthorSong(cursor);
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
            AuthorSong authorSong = cursorToAuthorSong(cursor);
            authors.add(authorSong);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        Log.d(this.getClass().getName(), "Author songs dao count" + authors.size());
        return authors;
    }


    public List<AuthorSong> findAuthorsFromAuthorBooks() {
        List<AuthorSong> authorSongList = new ArrayList<AuthorSong>();
        String havingStatement = "COUNT(*) > 1";
        Cursor cursor = getDatabase().query(true, TABLE_NAME_AUTHOR,
                columns, null, null, AUTHOR_ID, havingStatement, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AuthorSong authorSong = cursorToAuthorId(cursor);
            authorSongList.add(authorSong);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return authorSongList;
    }


    private AuthorSong cursorToAuthorSong(Cursor cursor)
    {
        AuthorSong authorSong = new AuthorSong();
        authorSong.setAuthorId(cursor.getInt(0));
        authorSong.setSongId(cursor.getInt(1));
        return authorSong;
    }

    private AuthorSong cursorToAuthorId(Cursor cursor) {
        AuthorSong authorSong = new AuthorSong();
        authorSong.setAuthorId(cursor.getInt(0));
        return authorSong;
    }
}
