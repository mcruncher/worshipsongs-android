package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.UtilitiesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class SongDao extends AbstractDao
{
    public static final String TABLE_NAME = "songs";
    public static final String[] allColumns = {"id", "song_book_id", "title", "alternate_title",
            "lyrics", "verse_order", "copyright", "comments", "ccli_number", "song_number", "theme_name",
            "search_title", "search_lyrics", "create_date", "last_modified", "temporary"};
    private UtilitiesService utilitiesService = new UtilitiesService();


    public SongDao()
    {
        super();
    }

    public SongDao(Context context)
    {
        super(context);
    }

    public List<Song> findByTag(String tag)
    {
        List<Song> songList = new ArrayList<Song>();
        if (StringUtils.isNotBlank(tag)) {
            for (Song song : findAll()) {
                if (song.getLyrics().contains(tag)) {
                    songList.add(song);
                }
            }
        } else {
            songList.addAll(findAll());
        }
        Log.d(this.getClass().getName(), "No. of songs: " + songList.size());
        return songList;
    }

    public List<Song> findAll()
    {
        List<Song> songs = new ArrayList<Song>();
        Cursor cursor = getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments"}, null, null, null, null, "title");
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
        String whereClause = " title" + "=\"" + title + "\"";
        Cursor cursor = getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments"}, whereClause, null, null, null, null);
        cursor.moveToFirst();
        song = cursorToSong(cursor);
        cursor.close();
        return song;
    }

    public Song getSongById(int songId)
    {
        Song song = new Song();
        try {
            Log.d(this.getClass().getName(), "Song ID" + songId);
            String whereClause = " id=" + songId + ";";
            Cursor cursor = getDatabase().query(TABLE_NAME,
                    new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments"}, whereClause, null, null, null, null);
            cursor.moveToFirst();
            song = cursorToSong(cursor);
            Log.d(this.getClass().getName(), "Song:" + song);
            cursor.close();
        } catch (Exception e) {
            Log.d(this.getClass().getName(), "Exception to get song by id:" + e);
        } finally {
            return song;
        }
    }

    public List<Song> getSongTitlesByBookId(int songBookId)
    {
        List<Song> songs = new ArrayList<Song>();
        try {
            String whereClause = " song_book_id=" + songBookId + ";";
            Cursor cursor = getDatabase().query(TABLE_NAME,
                    new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments"}, whereClause, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Song song = cursorToSong(cursor);
                songs.add(song);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(this.getClass().getName(), "Exception to get song by id:" + e);
        } finally {
            return songs;
        }
    }

    private Song cursorToSong(Cursor cursor)
    {
        Song song = new Song();
        song.setTitle(cursor.getString(0));
        song.setLyrics(cursor.getString(1));
        song.setVerseOrder(cursor.getString(2));
        song.setSearchTitle(cursor.getString(3));
        song.setSearchLyrics(cursor.getString(4));
        song.setComments(cursor.getString(5));

        return song;
    }

    public Song findContentsByTitle(String title)
    {
        Song song = getSongByTitle(title);
        String lyrics = song.getLyrics();
        ArrayList<String> contents = new ArrayList<>();
        List<Verse> verseList = utilitiesService.getVerse(lyrics);
        List<String> verseName = new ArrayList<String>();
        List<String> contentsByDefaultOrder = new ArrayList<String>();
        Map<String, String> verseDataMap = new HashMap<String, String>();
        for (Verse verses : verseList) {
            verseName.add(verses.getType() + verses.getLabel());
            contentsByDefaultOrder.add(verses.getContent());
            verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
        }
        List<String> contentsByVerseOrder = new ArrayList<String>();
        List<String> verseOrderList = new ArrayList<String>();
        String verseOrder = song.getVerseOrder();
        if (StringUtils.isNotBlank(verseOrder)) {
            verseOrderList = utilitiesService.getVerseByVerseOrder(verseOrder);
        }

        if (verseOrderList.size() > 0) {
            for (int i = 0; i < verseOrderList.size(); i++) {
                contentsByVerseOrder.add(verseDataMap.get(verseOrderList.get(i)));
            }
            contents.addAll(contentsByVerseOrder);
            Log.d(this.getClass().getName(), "Verse List data content :" + contentsByVerseOrder);
        } else {
            contents.addAll(contentsByDefaultOrder);
        }
        Song parsedSong = new Song();
        parsedSong.setContents(contents);
        parsedSong.setUrlKey(parseMediaUrlKey(song.getComments()));
        Log.d(this.getClass().getName(), "Parsed media url : " + parsedSong.getUrlKey());
        parsedSong.setChord(parseChord(song.getComments()));
        Log.d(this.getClass().getName(), "Parsed chord  : " + parsedSong.getChord());
        return parsedSong;
    }

    String parseMediaUrlKey(String comments)
    {
        // Log.i(this.getClass().getSimpleName(), "Preparing to parse media url: " + comments);
        String mediaUrl = "";
        if (comments != null && comments.length() > 0) {
            String[] commentArray = comments.split("\n");
            if (commentArray.length >= 1) {
                String mediaUrlLine = commentArray[0];
                String[] medialUrlArray = mediaUrlLine.split("=");
                if (medialUrlArray != null && medialUrlArray.length >= 3) {
                    mediaUrl = medialUrlArray[2];
                }
            }
        }
        return mediaUrl;
    }

    String parseChord(String comments)
    {
        // Log.i(this.getClass().getSimpleName(), "Preparing to parse chord: " + comments);
        String chord = "";
        if (comments != null && comments.length() > 0) {
            String[] commentArray = comments.split("\n");
            if (commentArray.length >= 2) {
                String chordLine = commentArray[1];
                String[] chordArray = chordLine.split("=");
                if (chordArray != null && chordArray.length >= 2) {
                    chord = chordArray[1];
                }
            }
        }
        return chord;
    }
}
