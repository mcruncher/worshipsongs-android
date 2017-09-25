package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.domain.Topics;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.RegexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongBookDao extends AbstractDao implements ISongBookDao
{
    public static final String TABLE_NAME = "song_books";
    public String[] allColumns = {"id", "name", "publisher"};
    public static final String SONG_BOOK_NAME_REGEX = "\\{.*\\}";

    private UserPreferenceSettingService userPreferenceSettingService = new UserPreferenceSettingService();

    public SongBookDao()
    {

    }

    public SongBookDao(Context context)
    {
        super(context);
    }

    @Override
    public List<SongBook> findAll()
    {
        List<SongBook> songBooks = new ArrayList<SongBook>();
        Cursor cursor = getDatabase().query(true, TABLE_NAME,
                allColumns, null, null, null, null, allColumns[1], null);
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
        return songBook;
    }

    String parseName(String name)
    {
        if (userPreferenceSettingService.isTamil()) {
            return getTamilName(name);
        } else {
            return getDefaultName(name);
        }
    }

    String getTamilName(String name)
    {
        if (StringUtils.isNotBlank(name)) {
            String tamilTopicName = RegexUtils.getMatchString(name, SONG_BOOK_NAME_REGEX);
            String formattedTopicName = tamilTopicName.replaceAll("\\{", "").replaceAll("\\}", "");
            return StringUtils.isNotBlank(formattedTopicName) ? formattedTopicName : name;
        }
        return "";
    }

    String getDefaultName(String name)
    {
        if (StringUtils.isNotBlank(name)) {
            return StringUtils.strip(name.replaceAll(SONG_BOOK_NAME_REGEX, ""));
        }
        return "";
    }
}
