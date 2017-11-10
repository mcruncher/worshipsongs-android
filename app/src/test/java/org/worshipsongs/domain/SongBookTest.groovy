package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongBookTest extends Specification
{
    def songBook1 = new SongBook()
    def songBook2 = new SongBook()

    void setup()
    {
        songBook1 = new SongBook("foo")
        songBook2 = new SongBook(songBook1.getName())
    }

    def "To string"()
    {
        setup:
        def result = songBook1.toString()

        expect:
        result.contains("foo")
        !result.contains("bar")
    }

    def "Equals"()
    {
        expect:
        songBook1.equals(songBook2)
    }

    def "Not equals"()
    {
        setup:
        songBook2.setName("bar")

        expect:
        !songBook1.equals(songBook2)
    }

    def "HashCode"()
    {
        setup:
        def songBooks = new HashSet<SongBook>()
        songBooks.add(songBook1)
        songBooks.add(songBook2)

        expect:
        songBooks.size() == 1
    }
}
