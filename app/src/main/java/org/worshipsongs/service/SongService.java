package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.dao.SongDao;
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
        List<Song> filteredSongs = new ArrayList<>(filteredSongSet);
        Collections.sort(filteredSongs, new SongComparator());
        return filteredSongs;
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
            return song1.getTitle().compareTo(song2.getTitle());
        }
    }
}
