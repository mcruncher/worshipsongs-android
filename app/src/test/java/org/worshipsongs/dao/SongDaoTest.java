package org.worshipsongs.dao;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println("--parseMediaUrlKey--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";
        assertEquals("Ro59iCBNBdI", songDao.parseMediaUrlKey(comments));
    }

    @Test
    public void testParseMediaUrlKey_ChordDefined() throws Exception
    {
        System.out.println("--parseMediaUrlKey_ChordDefined--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\nchord=c";
        assertEquals("Ro59iCBNBdI", songDao.parseMediaUrlKey(comments));
    }

    @Test
    public void testParseMediaUrlKey_NewLine() throws Exception
    {
        System.out.println("--parseMediaUrlKey_NewLine--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n";
        assertEquals("Ro59iCBNBdI", songDao.parseMediaUrlKey(comments));
    }

    @Test
    public void testParseChord()
    {
        System.out.println("--parseChord--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n" +
                "chord=G";
        assertEquals("G", songDao.parseChord(comments));
    }
    @Test
    public void testParseChord_SingleLine()
    {
        System.out.println("--parseChord_SingleLine--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdIchord=g";
        assertEquals("", songDao.parseChord(comments));
    }


    @Test
    public void testParseChord_NotDefined()
    {
        System.out.println("--parseChord_NotDefined--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n";
        assertEquals("", songDao.parseChord(comments));
    }

    @Test
    public void testParseChord_NotDefinedNoNewLine()
    {
        System.out.println("--parseChord_NotDefinedNoNewLine--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";
        assertEquals("", songDao.parseChord(comments));
    }
}