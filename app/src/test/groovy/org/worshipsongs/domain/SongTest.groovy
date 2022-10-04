package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongTest extends Specification
{
    def song1;
    def song2;

    def setup()
    {
        song1 = new Song("foo")
        song2 = new Song(song1.getTitle())
    }

    def "To string"()
    {
        setup:
        def result = song1.toString()

        expect:
        result.contains("foo")
        !result.contains("Song1")
    }

    def "Equals"()
    {
        expect:
        song1.equals(song2)
    }

    def "Not equals"()
    {
        setup:
        song2.setTitle("bar")

        expect:
        !song1.equals(song2)
    }

    def "Hash code"()
    {
        setup:
        def set = new HashSet<Song>()
        set.add(song1)
        set.add(song2)

        expect:
        set.size()
    }
}
