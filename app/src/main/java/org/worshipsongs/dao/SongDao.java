package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class SongDao extends AbstractDao
{
    public static final String TABLE_NAME_AUTHOR = "songs";
    public static final String[] allColumns = {"id", "song_book_id", "title", "alternate_title",
            "lyrics", "verse_order", "copyright", "comments", "ccli_number", "song_number", "theme_name",
            "search_title", "search_lyrics", "create_date", "last_modified", "temporary"};

    public SongDao(Context context)
    {
        super(context);
    }

    public List<Song> findByTag(String tag)
    {
        List<Song> songList = new ArrayList<Song>();
        if (StringUtils.isNotBlank(tag)) {
            for (Song song : findTitles()) {
                if (song.getLyrics().contains(tag)) {
                    songList.add(song);
                }
            }
        } else {
            songList.addAll(findTitles());
        }
        Log.d(this.getClass().getName(), "No. of songs: " + songList.size());
        return songList;
    }

    public List<Song> findTitles()
    {
        List<Song> songs = new ArrayList<Song>();
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                new String[]{"title", "lyrics", "verse_order"}, null, null, null, null, "title");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            songs.add(song);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

    public Song getSongByTitle(String title)
    {
        Song song = new Song();
        String whereClause = " title" + "=\""+title+"\"";
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                new String[]{"title", "lyrics", "verse_order"},whereClause, null, null, null, null);
        cursor.moveToFirst();
        song = cursorToSong(cursor);
        cursor.close();
        return song;
    }

    public Song getSongById(int songId)
    {
        Song song = new Song();
        try{
            Log.d(this.getClass().getName(), "Song ID" + songId);
            String whereClause = " id="+songId+";";
            Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                    new String[]{"title", "lyrics", "verse_order"},whereClause, null, null, null, null);
            cursor.moveToFirst();
            song = cursorToSong(cursor);
            Log.d(this.getClass().getName(), "Song:" + song);
            cursor.close();
        }
        catch (Exception e){
            Log.d(this.getClass().getName(), "Exception to get song by id:" + e);
        }
        finally {
            return song;
        }
    }

    public List<Song> getSongTitlesByBookId(int songBookId)
    {
        List<Song> songs = new ArrayList<Song>();
        try{
            Log.d(this.getClass().getName(), "Song ID" + songBookId);
            String whereClause = " song_book_id="+songBookId+";";
            Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                    new String[]{"title", "lyrics", "verse_order"},whereClause, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Song song = cursorToSong(cursor);
                songs.add(song);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e){
            Log.d(this.getClass().getName(), "Exception to get song by id:" + e);
        }
        finally {
            return songs;
        }
    }

    private Song cursorToSong(Cursor cursor)
    {
        Song song = new Song();
        song.setTitle(cursor.getString(0));
        song.setLyrics(cursor.getString(1));
        song.setVerseOrder(cursor.getString(2));
        return song;
    }
}
