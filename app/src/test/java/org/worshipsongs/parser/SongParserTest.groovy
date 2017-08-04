package org.worshipsongs.parser

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class SongParserTest extends Specification
{
    def songParser = new SongParser()

    def "Regex pattern"()
    {
        expect:
        SongParser.I_18_N_TITLE_REGEX == "i18nTitle.*"
        SongParser.MEDIA_URL_REGEX == "mediaurl.*"
        SongParser.CHORD_REGEX == "originalKey.*"
    }

    def "Parse media url key"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI"

        expect:
        songParser.parseMediaUrlKey(comments) == "Ro59iCBNBdI"
    }

    def "Parse media url when chord defined"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\noriginalKey=c"

        expect:
        songParser.parseMediaUrlKey(comments) == "Ro59iCBNBdI"
    }

    def "Parse media url key when have new line"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n"

        expect:
        songParser.parseMediaUrlKey(comments) == "Ro59iCBNBdI"
    }

    def "Parse media url key from null"()
    {
        expect:
        songParser.parseMediaUrlKey(null) == ""
    }

    def "Parse media url key from empty string"()
    {
        expect:
        songParser.parseMediaUrlKey("") == ""
    }

    def "Parse chord"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n" + "originalKey=G"

        expect:
        songParser.parseChord(comments) == "G"
    }

    def "Parse chord when present in first"()
    {
        setup:
        def comments = "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI";

        expect:
        songParser.parseChord(comments) == "g"
    }

    def "Parse chord when not defined"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n";

        expect:
        songParser.parseChord(comments) == ""
    }

    def "Parse chord from null"()
    {
        expect:
        songParser.parseChord(null) == ""
    }

    def "Parse chord from empty string"()
    {
        expect:
        songParser.parseChord("") == ""
    }

    def "Parse tamil title"()
    {
        setup:
        def comments = "i18nTitle=இடைவிடா நன்றி உமக்குத்தானே"

        expect:
        songParser.parseTamilTitle(comments) == "இடைவிடா நன்றி உமக்குத்தானே"
    }

    def "Parse tamil title when not defined"()
    {
        setup:
        def comments = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI"

        expect:
        songParser.parseTamilTitle(comments) == ""
    }

    def "Parse tamil title when defined in end of comments"()
    {
        setup:
        def comments = "originalKey=g\nmediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\ni18nTitle=இடைவிடா நன்றி உமக்குத்தானே"

        expect:
        songParser.parseTamilTitle(comments) == "இடைவிடா நன்றி உமக்குத்தானே"
    }

    def "Parse tamil title from empty string"()
    {
        expect:
        songParser.parseTamilTitle("") == ""
    }

    def "Parse tamil title from null"()
    {
        expect:
        songParser.parseTamilTitle(null) == ""
    }

}
