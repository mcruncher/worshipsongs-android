package org.worshipsongs.service;



import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.*;

/**
 * @author : Madasamy
 * @since : 3.x
 */

@RunWith(AndroidJUnit4.class)
public class SongBookServiceTest
{
    private SongBookService songBookService;

    @Before
    public void setUp() throws Exception
    {
        songBookService = new SongBookService(getTargetContext());
    }

    @Test
    public void findAll() throws Exception
    {
        System.out.println("--findAll--");
        assertEquals(2, songBookService.findAll().size());
    }

}