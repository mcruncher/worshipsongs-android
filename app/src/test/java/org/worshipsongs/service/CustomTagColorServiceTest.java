package org.worshipsongs.service;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 2.5.1
 */
public class CustomTagColorServiceTest
{
    private CustomTagColorService customTagColorService = new CustomTagColorService();

    @Test
    public void getFormattedLines() {
        System.out.println("--getFormattedLines--");
        String content = "{y}ஆளுகை செய்யும் ஆவியானவரே {/y}\n" +
                "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                {y}பலியாய் தந்தேன் பரிசுத்தமானவரே{/y}\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                {y}ஆவியானவரே - என் ஆற்றலானவரே{/y}\n" +
                "                                Aaviyaanavarae - En aatralaanavarae";
//        List<String> result = customTagColorService.getFormattedLines(content);
//        assertEquals(6, result.size());
//        assertFalse(result.contains("{y}"));
//        assertFalse(result.contains("{/y}"));
    }

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