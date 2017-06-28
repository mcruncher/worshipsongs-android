package org.worshipsongs.dao;


import org.junit.Test;
import org.junit.runner.RunWith;

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
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\noriginalKey=c";
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
                "originalKey=G";
        assertEquals("G", songDao.parseChord(comments));
    }

    @Test
    public void testParseChord_ChordpresentInfirst()
    {
        System.out.println("--parseChord_SingleLine--");
        String comments = "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";
        assertEquals("g", songDao.parseChord(comments));
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

    @Test
    public void testParseTamilTitle()
    {
        System.out.println("--parseTamilTitle--");
        String comments = "i18nTitle=இடைவிடா நன்றி உமக்குத்தானே";
        assertEquals("இடைவிடா நன்றி உமக்குத்தானே", songDao.parseTamilTitle(comments));
    }

    @Test
    public void testParseTamilTitle_NotDefined()
    {
        System.out.println("--parseTamilTitle_NotDefined--");
        String comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";
        assertEquals("", songDao.parseTamilTitle(comments));
    }

    @Test
    public void testParseTamilTitle_End()
    {
        System.out.println("--parseTamilTitle_End--");
        String comments = "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\ni18nTitle=இடைவிடா நன்றி உமக்குத்தானே";
        assertEquals("இடைவிடா நன்றி உமக்குத்தானே", songDao.parseTamilTitle(comments));
    }
}