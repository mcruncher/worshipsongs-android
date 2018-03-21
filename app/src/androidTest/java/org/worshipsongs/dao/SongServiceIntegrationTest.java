package org.worshipsongs.dao;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.SongService;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Author : Madasamy
 * Version : 2.x
 */

@RunWith(AndroidJUnit4.class)
public class SongServiceIntegrationTest
{
    private SongService songService;

    @Before
    public void setUp() throws Exception
    {
        songService = new SongService(getTargetContext());
    }

    @After
    public void tearDown() throws Exception
    {
       // songService.close();
    }

    @Test
    public void testFinAll()
    {
        System.out.println("--findAll--");
        List<Song> songs = songService.findAll();
        assertFalse(songs.isEmpty());
    }

    @Test
    public void testFindByTitle()
    {
        System.out.println("--findByTitle--");
        Song song = songService.findContentsByTitle("Allelooyaa Karththaraiyae");
        System.out.print("Song" + song.getContents());
        assertEquals("Allelooyaa Karththaraiyae", song.getTitle());
    }

    @Test
    public void testFindByWrongTitle()
    {
        System.out.println("--findByTitle--");
        Song song = songService.findContentsByTitle("foobar");
        assertNull(song);
    }

    @Test
    public void testCount()
    {
        System.out.println("--count--");
        assertEquals(733, songService.count());
    }

}
