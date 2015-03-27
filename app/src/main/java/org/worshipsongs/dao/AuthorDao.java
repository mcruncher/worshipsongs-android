package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;

import org.worshipsongs.domain.Author;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class AuthorDao extends AbstractDao
{
    public static final String TABLE_NAME_AUTHOR = "authors";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    private String[] allColumns = {COLUMN_ID, COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME, COLUMN_DISPLAY_NAME};

    public AuthorDao(Context context)
    {
        super(context);
    }

    public List<Author> findAll()
    {
        List<Author> authors = new ArrayList<Author>();
        Cursor cursor = getDatabase().query(true, TABLE_NAME_AUTHOR,
                allColumns, null, null, null, null, COLUMN_DISPLAY_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Author author = cursorToAuthor(cursor);
            authors.add(author);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return authors;
    }

    private Author cursorToAuthor(Cursor cursor)
    {
        Author author = new Author();
        author.setId(cursor.getInt(0));
        author.setFirstName(cursor.getString(1));
        author.setLastName(cursor.getString(2));
        author.setDisplayName(cursor.getString(3));
        return author;
    }
}
