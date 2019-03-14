package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Type;
import org.worshipsongs.parser.ISongParser;
import org.worshipsongs.parser.SongParser;
import org.worshipsongs.service.DatabaseService;
import org.worshipsongs.service.UserPreferenceSettingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class SongService
{
    public static final String TABLE_NAME = "songs";
    private ISongParser songParser = new SongParser();
    private DatabaseService databaseService;
    private UserPreferenceSettingService userPreferenceSettingService;
    private SharedPreferences sharedPreferences;

    public SongService(Context context)
    {
        databaseService = new DatabaseService(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userPreferenceSettingService = new UserPreferenceSettingService(context);
    }

    public List<Song> findAll()
    {
        List<Song> songs = new ArrayList<Song>();
        Cursor cursor = databaseService.getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"},
                null, null, null, null, "title");
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
        String query = "select title,lyrics,verse_order,search_title,search_lyrics,comments, s.id " +
                "from songs as s inner join songs_topics as st on st.song_id = s.id inner join " +
                "topics as t on st.topic_id = t.id where t.id= ?";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, new String[]{String.valueOf(topicId)});
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
        String query = "select title,lyrics,verse_order,search_title,search_lyrics,comments,s.id " +
                "from songs as s inner join authors_songs as aus on aus.song_id = s.id inner join" +
                " authors as t on aus.author_id = t.id where t.id= ?";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, new String[]{String.valueOf(authorId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            songs.add(song);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

    public List<Song> findBySongBookId(int songBookId)
    {
        List<Song> songs = new ArrayList<>();
        String query = "select title,lyrics,verse_order,search_title,search_lyrics,comments,s.id, entry  from " +
                "songs as s inner join songs_songbooks as ssb on ssb.song_id = s.id inner join " +
                "song_books as sb on ssb.songbook_id = sb.id where sb.id= ?";
        Cursor cursor = databaseService.getDatabase().rawQuery(query, new String[]{String.valueOf(songBookId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            song.setSongBookNumber(getSongBookNo(cursor));
            songs.add(song);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

    private int getSongBookNo(Cursor cursor)
    {
        try {
            String numberInString = cursor.getString(7);
            return Integer.parseInt(numberInString);
        } catch (Exception ex) {
            return 0;
        }
    }

    public Song findContentsByTitle(String title)
    {
        Song song = findByTitle(title);
        if (song != null) {
            Song parsedSong = new Song();
            parsedSong.setTitle(title);
            parsedSong.setLyrics(song.getLyrics());
            parsedSong.setVerseOrder(song.getVerseOrder());
            parsedSong.setSearchTitle(song.getSearchTitle());
            parsedSong.setSearchLyrics(song.getSearchLyrics());
            parsedSong.setComments(song.getComments());
            parsedSong.setContents(songParser.parseContents(WorshipSongApplication.getContext(), song.getLyrics(), song.getVerseOrder()));
            parsedSong.setUrlKey(songParser.parseMediaUrlKey(song.getComments()));
            parsedSong.setChord(songParser.parseChord(song.getComments()));
            parsedSong.setTamilTitle(songParser.parseTamilTitle(song.getComments()));
            parsedSong.setId(song.getId());
            return parsedSong;
        }
        return null;
    }

    public Song findByTitle(String title)
    {
        Song song = null;
        String whereClause = " title" + "=\"" + title + "\"";
        Cursor cursor = databaseService.getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"},
                whereClause, null, null, null, "title");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            song = cursorToSong(cursor);
            cursor.close();
        }
        return song;
    }


    public Song findById(int id)
    {
        Song song = null;
        String whereClause = " id" + "=" + id + "";
        Cursor cursor = databaseService.getDatabase().query(TABLE_NAME,
                new String[]{"title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"},
                whereClause, null, null, null, "title");
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
        song.setId(cursor.getInt(6));
        song.setUrlKey(songParser.parseMediaUrlKey(song.getComments()));
        song.setChord(songParser.parseChord(song.getComments()));
        song.setTamilTitle(songParser.parseTamilTitle(song.getComments()));
        return song;
    }

    public long count()
    {
        Cursor c = databaseService.getDatabase().query(TABLE_NAME, null, null,
                null, null, null, null);
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


    public List<Song> filterSongs(String type, String query, List<Song> songs)
    {
        Set<Song> filteredSongSet = new HashSet<>();
        if (StringUtils.isBlank(query)) {
            filteredSongSet.addAll(songs);
        } else {
            for (Song song : songs) {
                if (sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true)) {
                    if (getTitles(song.getSearchTitle()).toString().toLowerCase().contains(query.toLowerCase()) ||
                            String.valueOf(song.getSongBookNumber()).equalsIgnoreCase(query)) {
                        filteredSongSet.add(song);
                    }
                } else {
                    if (song.getSearchLyrics().toLowerCase().contains(query.toLowerCase())) {
                        filteredSongSet.add(song);
                    }
                }
                if (song.getComments() != null && song.getComments().toLowerCase().contains(query.toLowerCase())) {
                    filteredSongSet.add(song);
                }
            }
        }
        return getSortedSongs(type, filteredSongSet);
    }

    boolean isSearchBySongBookNumber(String type, String query)
    {
        int songBookNumber = getSongBookNumber(query);
        return Type.SONG_BOOK.name().equalsIgnoreCase(type) && songBookNumber >= 0 && sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true);
    }

    int getSongBookNumber(String query)
    {
        try {
            return Integer.parseInt(query);
        } catch (Exception ex) {
            return -1;
        }
    }

    public List<ServiceSong> filteredServiceSongs(String query, List<ServiceSong> serviceSongs)
    {
        List<ServiceSong> filteredServiceSongs = new ArrayList<>();
        if (StringUtils.isBlank(query)) {
            filteredServiceSongs.addAll(serviceSongs);
        } else if (serviceSongs != null && !serviceSongs.isEmpty()) {
            for (ServiceSong serviceSong : serviceSongs) {
                if (getSearchTitles(serviceSong).toString().toLowerCase().contains(query.toLowerCase())) {
                    filteredServiceSongs.add(serviceSong);
                }
                if (serviceSong.getSong() != null && serviceSong.getSong().getComments() != null &&
                        serviceSong.getSong().getComments().toLowerCase().contains(query.toLowerCase())) {
                    filteredServiceSongs.add(serviceSong);
                }
            }
        }
        return filteredServiceSongs;
    }

    List<String> getSearchTitles(ServiceSong serviceSong)
    {
        List<String> searchTitles = new ArrayList<>();
        if (serviceSong != null && serviceSong.getSong() != null && StringUtils.isNotBlank(serviceSong.getSong().getSearchTitle())) {
            searchTitles.addAll(getTitles(serviceSong.getSong().getSearchTitle()));
        }
        return searchTitles;
    }

    List<Song> getSortedSongs(String type, Set<Song> filteredSongSet)
    {
        if (Type.SONG_BOOK.name().equalsIgnoreCase(type)) {
            List<Song> songs = new ArrayList<>(filteredSongSet);
            Collections.sort(songs, Song.SONG_BOOK_NUMBER_ASC);
            return songs;
        } else {
            return getSortedSongs(filteredSongSet);
        }
    }

    @NonNull
    private List<Song> getSortedSongs(Set<Song> filteredSongSet)
    {
        List<Song> tamilSongs = new ArrayList<>();
        List<Song> englishSongs = new ArrayList<>();
        for (Song song : filteredSongSet) {
            if (StringUtils.isNotBlank(song.getTamilTitle())) {
                tamilSongs.add(song);
            } else {
                englishSongs.add(song);
            }
        }
        Collections.sort(tamilSongs, new SongComparator());
        Collections.sort(englishSongs, new SongComparator());
        List<Song> sortedSongs = new ArrayList<>();
        sortedSongs.addAll(tamilSongs);
        sortedSongs.addAll(englishSongs);
        return sortedSongs;
    }

    List<String> getTitles(String searchTitle)
    {
        return Arrays.asList(searchTitle.split("@"));
    }

    public String getTitle(boolean isTamil, ServiceSong serviceSong)
    {
        try {
            return (isTamil && StringUtils.isNotBlank(serviceSong.getSong().getTamilTitle())) ?
                    serviceSong.getSong().getTamilTitle() : serviceSong.getTitle();
        } catch (Exception ex) {
            return serviceSong.getTitle();
        }
    }

    private class SongComparator implements Comparator<Song>
    {
        @Override
        public int compare(Song song1, Song song2)
        {
            if (userPreferenceSettingService.isTamil()) {
                int result = nullSafeStringComparator(song1.getTamilTitle(), song2.getTamilTitle());
                if (result != 0) {
                    return result;
                }
                return nullSafeStringComparator(song1.getTitle(), song2.getTitle());
            } else {
                return song1.getTitle().compareTo(song2.getTitle());
            }
        }

        private int nullSafeStringComparator(final String one, final String two)
        {
            if (StringUtils.isBlank(one) ^ StringUtils.isBlank(two)) {
                return (StringUtils.isBlank(one)) ? -1 : 1;
            }
            if (StringUtils.isBlank(one) && StringUtils.isBlank(one)) {
                return 0;
            }
            return one.toLowerCase().trim().compareTo(two.toLowerCase().trim());
        }

    }

}
