package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class SongService implements ISongService
{
    private SongDao songDao;
    private SharedPreferences sharedPreferences;
    private UserPreferenceSettingService userPreferenceSettingService;

    public SongService(Context context)
    {
        songDao = new SongDao(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userPreferenceSettingService = new UserPreferenceSettingService();
    }

    @Override
    public List<Song> findAll()
    {
        return songDao.findAll();
    }

    @Override
    public List<Song> filterSongs(String text, List<Song> songs)
    {
        Set<Song> filteredSongSet = new HashSet<>();
        if (StringUtils.isBlank(text)) {
            filteredSongSet.addAll(songs);
        } else {
            for (Song song : songs) {
                if (sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true)) {
                    if (getTitles(song.getSearchTitle()).toString().toLowerCase().contains(text.toLowerCase())) {
                        filteredSongSet.add(song);
                    }
                } else {
                    if (song.getSearchLyrics().toLowerCase().contains(text.toLowerCase())) {
                        filteredSongSet.add(song);
                    }
                }
                if (song.getComments() != null && song.getComments().toLowerCase().contains(text.toLowerCase())) {
                    filteredSongSet.add(song);
                }
            }
        }
        return getSortedSongs(filteredSongSet);
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

    @Override
    public List<Song> findByAuthorId(int id)
    {
        return songDao.findByAuthorId(id);
    }

    @Override
    public List<Song> findByTopicId(int id)
    {
        return songDao.findByTopicId(id);
    }

    List<String> getTitles(String searchTitle)
    {
        return Arrays.asList(searchTitle.split("@"));
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

        public int nullSafeStringComparator(final String one, final String two)
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

   public String getTitle(boolean isTamil, ServiceSong serviceSong)
    {
        try {
            return (isTamil && StringUtils.isNotBlank(serviceSong.getSong().getTamilTitle())) ?
                    serviceSong.getSong().getTamilTitle() : serviceSong.getTitle();
        } catch (Exception ex) {
            return serviceSong.getTitle();
        }
    }
}
