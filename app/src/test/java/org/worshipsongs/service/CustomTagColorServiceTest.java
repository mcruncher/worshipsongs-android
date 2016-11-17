package org.worshipsongs.service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 2.5.1
 */
public class CustomTagColorServiceTest
{
    private CustomTagColorService customTagColorService = new CustomTagColorService();

    @Test
    public void removeSingleTag() throws Exception
    {
      String lyricsLine = "{y}song foo year {/y}";
      String expected = "song foo year ";
      assertEquals(expected, customTagColorService.removeTag(lyricsLine, "y"));
    }

    @Test
    public void removeCenterTag() throws Exception
    {
        String lyricsLine = "song foo {y}year {/y}";
        String expected = "song foo year ";
        assertEquals(expected, customTagColorService.removeTag(lyricsLine, "y"));
    }

}