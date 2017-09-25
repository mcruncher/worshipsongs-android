package org.worshipsongs.service

import org.worshipsongs.domain.SongBook
import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class SongBookServiceSpockTest extends Specification
{
    def songBookService = new SongBookService()

    def "Filtered song books"()
    {
        given:
        def songs = new ArrayList<SongBook>()
        songs.add(new SongBook("foo"))

        when:
        def result = songBookService.filteredSongBooks("fo", songs)

        then:
        result.size() == 1
    }

    def "Filtered song books unknown query"()
    {
        given:
        def songs = new ArrayList<SongBook>()
        songs.add(new SongBook("foo"))

        when:
        def result = songBookService.filteredSongBooks("ba", songs)

        then:
        result.size() == 0
    }
}
