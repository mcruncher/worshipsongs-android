package org.worshipsongs.dao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.domain.Column;
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
    public static final String TABLE_NAME_AUTHOR = "songs";
    public static final String[] allColumns = {"id", "song_book_id", "title", "alternate_title",
            "lyrics", "verse_order", "copyright", "comments", "ccli_number", "song_number", "theme_name",
            "search_title", "search_lyrics", "create_date", "last_modified", "temporary"};
    private UtilitiesService utilitiesService = new UtilitiesService();

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
        String whereClause = " title" + "=\"" + title + "\"";
        Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                new String[]{"title", "lyrics", "verse_order"}, whereClause, null, null, null, null);
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
            Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                    new String[]{"title", "lyrics", "verse_order"}, whereClause, null, null, null, null);
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
            Cursor cursor = getDatabase().query(TABLE_NAME_AUTHOR,
                    new String[]{"title", "lyrics", "verse_order"}, whereClause, null, null, null, null);
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

        return song;
    }

//
//
//    public void setVersesAndContent(Song song)
//    {
//
//        //Song song = songDao.getSongByTitle(selectedSong);
//        String lyrics = song.getLyrics();
//        List<Verse> verseList = utilitiesService.getVerse(lyrics);
//        List<Column> verseNames = new ArrayList<Column>();
//        List<Column> verseContent = new ArrayList<Column>();
//        Map<String, String> verseDataMap = new HashMap<String, String>();
//        for (Verse verses : verseList) {
//            verseNames.add(new Column( verses.getType() + verses.getLabel()));
//            verseContent.add(new Column( verses.getContent()));
//            verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
//        }
//        List<Column> verseListDataContent = new ArrayList<Column>();
//        List<Column> verseListData = new ArrayList<Column>();
//        String verseOrder = song.getVerseOrder();
//        if (StringUtils.isNotBlank(verseOrder)) {
//            List<String> verseByVerseOrder = utilitiesService.getVerseByVerseOrder(verseOrder);
//            for (String verse : verseByVerseOrder) {
//                verseListData.add(new Column(verse));
//            }
//        }
////        Intent intent = new Intent(application.getContext(), SongContentViewActivity.class);
////        intent.putExtra("serviceName", song.getTitle());
//        if (verseListData.size() > 0) {
//            song.setVerseColumns(verseListData);
////            intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
//            for (int i = 0; i < verseListData.size(); i++) {
//                //verseListDataContent.add(verseDataMap.get(verseListData.get(i).getName()));
//                verseListDataContent.add(new Column(verseDataMap.get(verseListData.get(i).getName())));
//            }
//            song.setContentColumns(verseListDataContent);
////            intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
//            //  Log.d(this.getClass().getName(), "Verse List data content :" + verseListDataContent);
//        } else {
//            song.setVerseColumns(verseNames);
//            song.setContentColumns(verseContent);
////            intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
////            intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
//        }
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        application.getContext().startActivity(intent);
   // }
}
