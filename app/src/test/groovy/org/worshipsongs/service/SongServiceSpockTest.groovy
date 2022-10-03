package org.worshipsongs.service

import android.preference.PreferenceManager
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.ServiceSong
import org.worshipsongs.domain.Song
import org.worshipsongs.domain.Type
import spock.lang.Ignore
import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
@Ignore
class SongServiceSpockTest extends Specification
{
    def songService = new SongService(RuntimeEnvironment.application.getApplicationContext())
    def songs = null
    def preferences = null

    void setup()
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext())
        songs = new ArrayList()
        def song = new Song();
        song.title = "foo";
        song.searchTitle = "foo @";
        song.searchLyrics = "foo foo";
        song.songBookNumber = 2
        songs.add(song);

        def song1 = new Song();
        song1.title = "bar"
        song1.searchTitle = "bar @"
        song1.searchLyrics = "foo bar"
        song1.songBookNumber = 34
        songs.add(song1);
    }

    void cleanup()
    {
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply()

    }

    def "Filter songs by song book number"()
    {
        setup:
        def result = songService.filterSongs(Type.SONG_BOOK.name(), "2", songs)

        expect:
        result.size() == 1
    }

    def "Filter songs by title"()
    {
        setup:
        def result = songService.filterSongs(Type.SONG.name(), "foo", songs)

        expect:
        result.size() == 1
    }

    def "Filter songs by null"()
    {
        setup:
        def result = songService.filterSongs(Type.SONG.name(), "", songs)

        expect:
        result.size() == 2
    }

    def "Filter songs by contents"()
    {
        given:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, false).apply()

        when:
        def result = songService.filterSongs(Type.SONG.name(), "foo", songs)

        then:
        result.size() == 2
    }

    def "Is search by song book number"()
    {
        setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply()

        expect:
        songService.isSearchBySongBookNumber(Type.SONG_BOOK.name(), "2")
    }

    def "It Is search by song book number when string query"()
    {
        setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply()

        expect:
        !songService.isSearchBySongBookNumber(Type.SONG_BOOK.name(), "que")
    }

    def "It Is search by song book number when search by content"()
    {
        setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, false).apply()

        expect:
        !songService.isSearchBySongBookNumber(Type.SONG_BOOK.name(), "2")
    }

    def "Get song book number"()
    {
        setup:
        def result = songService.getSongBookNumber("98")

        expect:
        result == 98
    }

    def "Get default song book number"()
    {
        setup:
        def result = songService.getSongBookNumber("quer")

        expect:
        result == -1
    }

    def "Songs sort by song book number"()
    {
        setup:
        def result = songService.getSortedSongs(Type.SONG_BOOK.name(), new HashSet<Song>(songs))

        expect:
        result[0].songBookNumber == 2
        result[1].songBookNumber == 34
    }

    def "Songs sort by title"()
    {
        setup:
        def result = songService.getSortedSongs(Type.SONG.name(), new HashSet<Song>(songs))

        expect:
        result[0].title == "bar"
        result[1].title == "foo"
    }

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
