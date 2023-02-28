package org.worshipsongs.parser

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *  Author : James Selvakumar
 *  Version : 3.6.0
 */

class SongParserTest {
    val songParser = SongParser()

    @Test
    fun `Regex pattern`() {
        // // expect:
        assertEquals("verse", SongParser.XML_FILE_NAME)
        assertEquals("xml", SongParser.EXTENSION)
        assertEquals("verse", SongParser.VERSE_ELEMENT_NAME)
        assertEquals("label", SongParser.LABEL_ATTRIBUTE_NAME)
        assertEquals("type", SongParser.TYPE_ATTRIBUTE_NAME)

        assertEquals("i18nTitle.*", SongParser.I_18_N_TITLE_REGEX)
        assertEquals("mediaurl.*", SongParser.MEDIA_URL_REGEX)
        assertEquals("originalKey.*", SongParser.CHORD_REGEX)
    }

    @Test
    fun `Parse media url key`() {
        // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI"

        // expect:
        assertEquals("Ro59iCBNBdI", songParser.parseMediaUrlKey(comments))
    }

    @Test
    fun `Parse media url when chord defined`() {
        // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\noriginalKey=c"

        // expect:
        assertEquals("Ro59iCBNBdI", songParser.parseMediaUrlKey(comments))
    }

    @Test
    fun `Parse media url key when have new line`() {
        // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n"

        // expect:
        assertEquals("Ro59iCBNBdI", songParser.parseMediaUrlKey(comments))
    }

    @Test
    fun `Parse media url key from null`() {
        // expect:
        assertEquals("", songParser.parseMediaUrlKey(null))
    }

    @Test
    fun `Parse media url key from empty string`() {
        // expect:
        assertEquals("", songParser.parseMediaUrlKey(""))
    }

    @Test
    fun `Parse chord`() {
        // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n" + "originalKey=G"

        // expect:
        assertEquals("G", songParser.parseChord(comments))
    }

    @Test
    fun `Parse chord when present in first`() {
        // setup:
        val comments = "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";

        // expect:
        assertEquals("g", songParser.parseChord(comments))
    }

    @Test
    fun `Parse chord when not funined`() {
        // // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n";

        // // expect:
        assertEquals("", songParser.parseChord(comments))
    }

    @Test
    fun `Parse chord from null`() {
        // // expect:
        assertEquals("", songParser.parseChord(null))
    }

    @Test
    fun `Parse chord from empty string`() {
        // // expect:
        assertEquals("", songParser.parseChord(""))
    }

    @Test
    fun `Parse tamil title`() {
        // // setup:
        val comments = "i18nTitle=இடைவிடா நன்றி உமக்குத்தானே"

        // // expect:
        assertEquals("இடைவிடா நன்றி உமக்குத்தானே", songParser.parseTamilTitle(comments))
    }

    @Test
    fun `Parse tamil title when not funined`() {
        // // setup:
        val comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI"

        // // expect:
        assertEquals("", songParser.parseTamilTitle(comments))
    }

    @Test
    fun `Parse tamil title when funined in end of comments`() {
        // // setup:
        val comments =
            "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\ni18nTitle=இடைவிடா நன்றி உமக்குத்தானே"

        // // expect:
        assertEquals("இடைவிடா நன்றி உமக்குத்தானே", songParser.parseTamilTitle(comments))
    }

    @Test
    fun `Parse tamil title from empty string`() {
        // // expect:
        assertEquals("", songParser.parseTamilTitle(""))
    }

    @Test
    fun `Parse tamil title from null`() {
        // // expect:
        assertEquals("", songParser.parseTamilTitle(null))
    }
}
