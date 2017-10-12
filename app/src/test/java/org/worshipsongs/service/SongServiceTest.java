package org.worshipsongs.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.BuildConfig;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class SongServiceTest
{
    private SongService oldSongService;
    private List<Song> songs;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() throws IOException
    {
        oldSongService = new SongService(RuntimeEnvironment.application.getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        songs = new ArrayList<>();
        Song song = new Song();
        song.setTitle("foo");
        song.setSearchTitle("foo @");
        song.setSearchLyrics("foo foo");

        songs.add(song);

        Song song1 = new Song();
        song1.setTitle("foo bar");
        song1.setSearchTitle("bar @");
        song1.setTitle("bar");
        song1.setSearchLyrics("foo bar");
        songs.add(song1);
    }

    @After
    public void tearDown()
    {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void getTitles() throws Exception
    {
        System.out.println("--getTitles--");
        String searchTitle = "foo@foo @bar";
        assertEquals(3, oldSongService.getTitles(searchTitle).size());
    }

    @Test
    public void testGetDefaultTitle()
    {
        System.out.println("--getDefaultTitle--");
        Song song = new Song();
        ServiceSong serviceSong = new ServiceSong("foo", song);
        String title = oldSongService.getTitle(false, serviceSong);
        assertEquals("foo", title);
    }

    @Test
    public void testGetTamilTitle()
    {
        System.out.println("--getDefaultTitle--");
        Song song = new Song();
        song.setTamilTitle("தமிழ்");
        ServiceSong serviceSong = new ServiceSong("foo", song);
        String title = oldSongService.getTitle(true, serviceSong);
        assertEquals("தமிழ்", title);
    }

    @Test
    public void testGetTitleFromNullObject()
    {
        System.out.println("--getDefaultTitle--");
        ServiceSong serviceSong = new ServiceSong("foo", null);
        String title = oldSongService.getTitle(true, serviceSong);
        assertEquals("foo", title);
    }

    @Test
    public void testGetTitleFromEmptyObject()
    {
        System.out.println("--getDefaultTitle--");
        ServiceSong serviceSong = new ServiceSong("foo", new Song());
        String title = oldSongService.getTitle(true, serviceSong);
        assertEquals("foo", title);
    }

}