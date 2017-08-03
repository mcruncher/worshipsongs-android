package org.worshipsongs.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.utils.RegexUtils;

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
    private static final String I18NTITLE_REGEX = "i18nTitle.*";
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

    public List<Song> findByTopicId(int topicId)
    {
        List<Song> songs = new ArrayList<Song>();
        String query = "select title,lyrics,verse_order,search_title,search_lyrics,comments " +
                "from songs as s inner join songs_topics as st on st.song_id = s.id inner join " +
                "topics as t on st.topic_id = t.id where t.id= ?";
        Cursor cursor = getDatabase().rawQuery(query, new String[]{String.valueOf(topicId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            songs.add(song);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

    public List<Song> findByAuthorId(int authorId)
    {
        List<Song> songs = new ArrayList<Song>();
        String query = "select title,lyrics,verse_order,search_title,search_lyrics,comments " +
                "from songs as s inner join authors_songs as aus on aus.song_id = s.id inner join" +
                " authors as t on aus.author_id = t.id where t.id= ?";
        Cursor cursor = getDatabase().rawQuery(query, new String[]{String.valueOf(authorId)});
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
        Song song = null;
        String whereClause = " title" + "=\"" + title + "\"";
        Cursor cursor = getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments"}, whereClause, null, null, null, "title");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            song = cursorToSong(cursor);
            cursor.close();
        }
        return song;
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
        song.setUrlKey(parseMediaUrlKey(song.getComments()));
        song.setChord(parseChord(song.getComments()));
        song.setTamilTitle(parseTamilTitle(song.getComments()));
        return song;
    }

    public Song findContentsByTitle(String title)
    {
        Song song = getSongByTitle(title);
        if (song != null) {
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
            parsedSong.setTitle(title);
            parsedSong.setLyrics(song.getLyrics());
            parsedSong.setVerseOrder(song.getVerseOrder());
            parsedSong.setSearchTitle(song.getSearchTitle());
            parsedSong.setSearchLyrics(song.getSearchLyrics());
            parsedSong.setComments(song.getComments());
            parsedSong.setContents(contents);
            parsedSong.setUrlKey(parseMediaUrlKey(song.getComments()));
            Log.d(this.getClass().getName(), "Parsed media url : " + parsedSong.getUrlKey());
            parsedSong.setChord(parseChord(song.getComments()));
            Log.d(this.getClass().getName(), "Parsed chord  : " + parsedSong.getChord());
            parsedSong.setTamilTitle(parseTamilTitle(song.getComments()));
            return parsedSong;
        }
        return song;
    }

    String parseMediaUrlKey(String comments)
    {
        // Log.i(this.getClass().getSimpleName(), "Preparing to parse media url: " + comments);
        String mediaUrl = "";
        if (comments != null && comments.length() > 0) {
            String mediaUrlLine = RegexUtils.getMatchString(comments, "mediaurl" + ".*");
            String[] medialUrlArray = mediaUrlLine.split("=");
            if (medialUrlArray != null && medialUrlArray.length >= 3) {
                mediaUrl = medialUrlArray[2];
            }
        }
        return mediaUrl;
    }

    String parseChord(String comments)
    {
        // Log.i(this.getClass().getSimpleName(), "Preparing to parse chord: " + comments);
        String chord = "";
        if (comments != null && comments.length() > 0) {
            String chordLine = RegexUtils.getMatchString(comments, "originalKey" + ".*");
            String[] chordArray = chordLine.split("=");
            if (chordArray != null && chordArray.length >= 2) {
                chord = chordArray[1];
            }
        }
        return chord;
    }

    String parseTamilTitle(String comments)
    {
        String tamilTitle = "";
        if (comments != null && comments.length() > 0) {
            String tamilTitleLine = RegexUtils.getMatchString(comments, I18NTITLE_REGEX);
            String[] chordArray = tamilTitleLine.split("=");
            if (chordArray != null && chordArray.length >= 2) {
                tamilTitle = chordArray[1];
            }
        }
        return tamilTitle;
    }

    public long count()
    {
        Cursor c = getDatabase().query(TABLE_NAME, null, null, null, null, null, null);
        int result = c.getCount();
        c.close();
        return result;
    }

    public boolean isValidDataBase()
    {
        try {
            findAll();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
