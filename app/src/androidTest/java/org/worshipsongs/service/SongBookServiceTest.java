package org.worshipsongs.service;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.worshipsongs.dao.SongDao;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 4.x
 */

@RunWith(AndroidJUnit4.class)
public class SongBookServiceTest
{
    private ISongBookService songBookService;

    @Before
    public void setUp() throws Exception
    {
        songBookService = new SongBookService(getTargetContext());
    }

    @Test
    public void findAll() throws Exception
    {
        System.out.println("--findAll--");
        assertEquals(4, songBookService.findAll().size());
    }

}