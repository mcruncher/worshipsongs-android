package org.worshipsongs.dao;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.worshipsongs.domain.Song;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Author : Madasamy
 * Version : 2.x
 */

@RunWith(AndroidJUnit4.class)
public class SongDaoIntegrationTest
{
    private SongDao songDao;
    
    @Before
    public void setUp() throws Exception {
        songDao = new SongDao(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        songDao.close();
    }

    @Test
    public void testFinAll() {
        System.out.println("--findAll--");
        List<Song> songs = songDao.findAll();
        assertFalse(songs.isEmpty());
    }

    @Test
    public void testFindByTitle()
    {
        System.out.println("--findByTitle--");
        Song song = songDao.findContentsByTitle("Allelooyaa Karththaraiyae");
        System.out.print("Song"+song.getContents());
        assertEquals("Allelooyaa Karththaraiyae", song.getTitle());
    }

}
