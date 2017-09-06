package org.worshipsongs.service

import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.worshipsongs.domain.ServiceSong
import org.worshipsongs.domain.Song

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongServiceSpockTest extends ElectricSpecification
{
    def songService = new SongService(RuntimeEnvironment.application.getApplicationContext())

    def "Filter service songs by null"()
    {
        setup:
        def result = songService.filteredServiceSongs(null, new ArrayList<ServiceSong>())

        expect:
        result.isEmpty()
    }

    def "Filter service songs by empty query"()
    {
        setup:
        def result = songService.filteredServiceSongs("", new ArrayList<ServiceSong>())

        expect:
        result.isEmpty()
    }

    def "Filter service songs by unknown query"()
    {
        setup:
        def serviceSongs = new ArrayList<ServiceSong>()
        def song = new Song()
        song.setSearchTitle("foo @ bar")
        serviceSongs.add(new ServiceSong("", song))
        def result = songService.filteredServiceSongs("query", serviceSongs)

        expect:
        result.isEmpty()
    }

    def "Filter service songs"()
    {
        given:
        def serviceSongs = new ArrayList<ServiceSong>()
        def song1 = new Song()
        song1.setSearchTitle("foo @ bar")

        def song2 = new Song()
        song2.setComments("bar @bar")
        serviceSongs.add(new ServiceSong("", song1))
        serviceSongs.add(new ServiceSong("", song2))

        when:
        def result = songService.filteredServiceSongs("fo", serviceSongs)

        then:
        !result.isEmpty()
    }

    def "Filter multiple service songs"()
    {
        given:
        def serviceSongs = new ArrayList<ServiceSong>()
        def song1 = new Song()
        song1.setSearchTitle("foo @ bar")

        def song2 = new Song()
        song2.setComments("bar @bar")
        serviceSongs.add(new ServiceSong("", song1))
        serviceSongs.add(new ServiceSong("", song2))

        when:
        def result = songService.filteredServiceSongs("bar", serviceSongs)

        then:
        !result.isEmpty()
        result.size() == 2
    }

    def "Filter service songs when  list is null"()
    {
        setup:
        def result = songService.filteredServiceSongs("fo", null)

        expect:
        result.isEmpty()
    }

    def "Get search titles from null"()
    {
        setup:
        def result = songService.getSearchTitles(null)

        expect:
        result.isEmpty()
    }

    def "Get search titles from null song"()
    {
        setup:
        def result = songService.getSearchTitles(new ServiceSong("", null))

        expect:
        result.isEmpty()
    }

    def "Get search titles from null search title"()
    {
        setup:
        def result = songService.getSearchTitles(new ServiceSong("", new Song()))

        expect:
        result.isEmpty()
    }

    def "Get search titles from empty search title"()
    {
        setup:
        def song = new Song()
        song.setSearchTitle("")
        def result = songService.getSearchTitles(new ServiceSong("", song))

        expect:
        result.isEmpty()
    }

    def "Get search titles"()
    {
        setup:
        def song = new Song()
        song.setSearchTitle("foo @ bar")
        def result = songService.getSearchTitles(new ServiceSong("", song))

        expect:
        !result.isEmpty()
    }

}
