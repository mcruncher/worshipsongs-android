package org.worshipsongs.service;

import android.widget.TextView;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.worshipsongs.WorshipSongApplication;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 2.5.1
 */

@RunWith(RobolectricTestRunner.class)
public class CustomTagColorServiceTest
{
    private CustomTagColorService customTagColorService = new CustomTagColorService();

    @Test
    public void testGetFormattedLines()
    {
        System.out.println("--getFormattedLines--");
        String content = "{y}ஆளுகை செய்யும் ஆவியானவரே {/y}\n" +
                "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                {y}பலியாய் தந்தேன் பரிசுத்தமானவரே{/y}\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                {y}ஆவியானவரே - என் ஆற்றலானவரே{/y}\n" +
                "                                Aaviyaanavarae - En aatralaanavarae";
        String expected = "ஆளுகை செய்யும் ஆவியானவரே \n" +
                "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                பலியாய் தந்தேன் பரிசுத்தமானவரே\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                ஆவியானவரே - என் ஆற்றலானவரே\n" +
                "                                Aaviyaanavarae - En aatralaanavarae\n";
        CustomTagColorService customTagColorService = new CustomTagColorService()
        {
            @Override
            protected boolean displayRomanisedLyrics()
            {
                return true;
            }

            @Override
            protected boolean displayTamilLyrics()
            {
                return true;
            }
        };
        String result = customTagColorService.getFormattedLines(content);
        assertEquals(expected, result);
    }

    @Test
    public void removeSingleTag() throws Exception
    {
        System.out.println("--removeSingleTag--");
        String lyricsLine = "{y}song foo year {/y}";
        String expected = "song foo year ";
        assertEquals(expected, customTagColorService.removeTag(lyricsLine, "y"));
    }

    @Test
    public void removeCenterTag() throws Exception
    {
        System.out.println("--removeCenterTag--");
        String lyricsLine = "song foo {y}year {/y}";
        String expected = "song foo year ";
        assertEquals(expected, customTagColorService.removeTag(lyricsLine, "y"));
    }

    @Test
    public void testRomanisedLyricsPreferences()
    {
        System.out.println("--romanisedLyricsPreferences--");
        final StringBuilder result = new StringBuilder();
        CustomTagColorService customTagColorService = new CustomTagColorService()
        {
            @Override
            protected boolean displayRomanisedLyrics()
            {
                return true;
            }

            @Override
            protected boolean displayTamilLyrics()
            {
                return false;
            }

            @Override
            protected void setColoredTextView(TextView textView, String content, int color)
            {
                result.append(content);
                result.append("\n");
            }
        };
        String content = "{y}ஆளுகை செய்யும் ஆவியானவரே {/y}\n" +
                "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                {y}பலியாய் தந்தேன் பரிசுத்தமானவரே{/y}\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                {y}ஆவியானவரே - என் ஆற்றலானவரே{/y}\n" +
                "                                Aaviyaanavarae - En aatralaanavarae";
        String expected = "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                Aaviyaanavarae - En aatralaanavarae\n";
        TextView textView = new TextView(WorshipSongApplication.Companion.getContext());
        customTagColorService.setCustomTagTextView(textView, content, 0xffffffff, 0xffffffff);
        assertEquals(expected, result.toString());
    }

    @Test
    public void testTamilLyricsPreferences()
    {
        System.out.println("--tamilLyricsPreferences--");
        final StringBuilder result = new StringBuilder();
        CustomTagColorService customTagColorService = new CustomTagColorService()
        {
            @Override
            protected boolean displayRomanisedLyrics()
            {
                return false;
            }

            @Override
            protected boolean displayTamilLyrics()
            {
                return true;
            }

            @Override
            protected void setColoredTextView(TextView textView, String content, int color)
            {
                result.append(content);
                result.append("\n");
            }
        };
        String content = "{y}ஆளுகை செய்யும் ஆவியானவரே {/y}\n" +
                "                                Aalugai seiyyum aaviyaanavarae\n" +
                "                                {y}பலியாய் தந்தேன் பரிசுத்தமானவரே{/y}\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                {y}ஆவியானவரே - என் ஆற்றலானவரே{/y}\n" +
                "                                Aaviyaanavarae - En aatralaanavarae";
        String expected = "ஆளுகை செய்யும் ஆவியானவரே \n" +
                "                                பலியாய் தந்தேன் பரிசுத்தமானவரே\n" +
                "                                ஆவியானவரே - என் ஆற்றலானவரே\n";
        TextView textView = new TextView(WorshipSongApplication.Companion.getContext());
        customTagColorService.setCustomTagTextView(textView, content, 0xffffffff, 0xffffffff);
        assertEquals(expected, result.toString());
    }

    @Test
    public void testIfNoTamilLyrics()
    {
        System.out.println("--ifNoTamilLyrics--");
        final StringBuilder result = new StringBuilder();
        CustomTagColorService customTagColorService = new CustomTagColorService()
        {
            @Override
            protected boolean displayRomanisedLyrics()
            {
                return false;
            }

            @Override
            protected boolean displayTamilLyrics()
            {
                return true;
            }

            @Override
            protected void setColoredTextView(TextView textView, String content, int color)
            {
                result.append(content);
                result.append("\n");
            }
        };
        String content = "Aalugai seiyyum aaviyaanavarae\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                Aaviyaanavarae - En aatralaanavarae";
        String expected = "Aalugai seiyyum aaviyaanavarae\n" +
                "                                Paliyaai thanthaen parisuththamaanavarae\n" +
                "                                Aaviyaanavarae - En aatralaanavarae\n";
        TextView textView = new TextView(WorshipSongApplication.Companion.getContext());
        customTagColorService.setCustomTagTextView(textView, content, 0xffffffff, 0xffffffff);
        assertEquals(expected, result.toString());
    }

}