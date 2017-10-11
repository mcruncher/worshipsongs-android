package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.DatabaseService;
import org.worshipsongs.utils.RegexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class AuthorService
{
    public static final String TABLE_NAME_AUTHOR = "authors";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    private String[] allColumns = {COLUMN_ID, COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME, COLUMN_DISPLAY_NAME};
    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String AUTHOR_NAME_REGEX = "\\{.*\\}";
    private String[] columns = {COLUMN_AUTHOR_ID};
    private DatabaseService databaseService;

    public AuthorService(Context context)
    {
        databaseService = new DatabaseService(context);
    }

    public List<Author> findAll()
    {
        List<Author> authors = new ArrayList<Author>();
        String query = "select t.id, t.first_name, t.last_name, t.display_name, count(t.id) " +
                "from songs as s inner join authors_songs as aus on aus.song_id = s.id inner join " +
                "authors as t on aus.author_id = t.id group by t.first_name ORDER by t.display_name";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Author author = cursorToAuthor(cursor);
            authors.add(author);
            cursor.moveToNext();
        }
        cursor.close();
        return authors;
    }

    private Author cursorToAuthor(Cursor cursor)
    {
        Author author = new Author();
        author.setId(cursor.getInt(0));
        author.setFirstName(cursor.getString(1));
        author.setLastName(cursor.getString(2));
        author.setName(cursor.getString(3));
        author.setNoOfSongs(cursor.getInt(4));
        author.setTamilName(databaseService.parseTamilName(author.getName()));
        author.setDefaultName(databaseService.parseEnglishName(author.getName()));
        return author;
    }

    public String findAuthorNameByTitle(String title)
    {
        String query = "select title, lyrics, verse_order, first_name, last_name, display_name" +
                " from songs as song , authors as author, authors_songs as authorsong where " +
                "song.id = authorsong.song_id and author.id = authorsong.author_id and song.title = ?";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, new String[]{title});
        cursor.moveToFirst();
        AuthorSong authorSong = getAuthorSong(cursor);
        return authorSong.getAuthor().getName();
    }

    private AuthorSong getAuthorSong(Cursor cursor)
    {
        Song song = new Song();
        song.setTitle(cursor.getString(0));
        song.setLyrics(cursor.getString(1));
        song.setVerseOrder(cursor.getString(2));

        Author author = new Author();
        author.setFirstName(cursor.getString(3));
        author.setLastName(cursor.getString(4));
        author.setName(cursor.getString(5));
        author.setTamilName(databaseService.parseTamilName(author.getName()));
        author.setDefaultName(databaseService.parseEnglishName(author.getName()));

        AuthorSong authorSong = new AuthorSong();
        authorSong.setSong(song);
        authorSong.setAuthor(author);
        return authorSong;
    }

    public List<Author> getAuthors(String text, List<Author> authorList)
    {
        List<Author> filteredAuthors = new ArrayList<Author>();
        if (StringUtils.isNotBlank(text)) {
            for (Author author : authorList) {
                if (author.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredAuthors.add(author);
                }
            }
        } else {
            filteredAuthors.addAll(authorList);
        }
        return filteredAuthors;
    }


}
