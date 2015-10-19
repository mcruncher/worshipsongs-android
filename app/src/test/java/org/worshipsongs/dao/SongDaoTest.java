package org.worshipsongs.dao;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: Madasamy
 * Version: 2.3.0
 */
public class SongDaoTest
{
    private SongDao songDao = new SongDao();

    @Test
    public void testParseMediaUrlKey() throws Exception
    {
        String mediaUrl = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";
        assertEquals("Ro59iCBNBdI", songDao.parseMediaUrlKey(mediaUrl));

    }
}