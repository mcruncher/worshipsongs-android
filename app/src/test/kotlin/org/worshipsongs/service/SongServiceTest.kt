package org.worshipsongs.service

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.ServiceSong
import org.worshipsongs.domain.Song
import org.worshipsongs.domain.Type

/**
 * Author : Madasamy
 * Version : x.x.x
 */
@RunWith(RobolectricTestRunner::class)
class SongServiceTest {
    var songService = SongService(ApplicationProvider.getApplicationContext())
    lateinit var songs: List<Song>
    var preferences =
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    @Before
    fun setUp() {
        val song1 = Song()
        song1.title = "foo"
        song1.searchTitle = "foo @"
        song1.searchLyrics = "foo foo"
        song1.songBookNumber = 2

        val song2 = Song()
        song2.title = "foo bar"
        song2.searchTitle = "bar @"
        song2.title = "bar"
        song2.searchLyrics = "foo bar"

        songs = listOf(song1, song2)
    }

    @After
    fun tearDown() {
        preferences.edit().clear().apply()
    }

    @Test
    fun titles() {
        val searchTitle = "foo@foo @bar"
        assertEquals(3, songService.getTitles(searchTitle).size.toLong())
    }

    @Test
    fun `Get valault title`() {
        val song = Song()
        val serviceSong = ServiceSong("foo", song)
        val title = songService.getTitle(false, serviceSong)
        assertEquals("foo", title)
    }

    @Test
    fun `Get tamil title`() {
        println("--getvalaultTitle--")
        val song = Song()
        song.tamilTitle = "தமிழ்"
        val serviceSong = ServiceSong("foo", song)
        val title = songService.getTitle(true, serviceSong)
        assertEquals("தமிழ்", title)
    }

    @Test
    fun `Get title from null object`() {
        println("--getvalaultTitle--")
        val serviceSong = ServiceSong("foo", null)
        val title = songService.getTitle(true, serviceSong)
        assertEquals("foo", title)
    }

    @Test
    fun `Get title from empty object`() {
        println("--getvalaultTitle--")
        val serviceSong = ServiceSong("foo", Song())
        val title = songService.getTitle(true, serviceSong)
        assertEquals("foo", title)
    }

    @Test
    fun `Filter songs by song book number`() {
        // setup:
        val result = songService.filterSongs(Type.SONG_BOOK.name, "2", songs!!)

        // expect:
        assertEquals(1, result.size.toLong())
    }

    @Test
    fun `Filter songs by title`() {
        // setup:
        val result = songService.filterSongs(Type.SONG.name, "foo", songs!!)

        // expect:
        assertEquals(1, result.size.toLong())
    }

    @Test
    fun `Filter songs by empty query`() {
        // setup:
        val result = songService.filterSongs(Type.SONG.name, "", songs!!)

        // expect:
        assertEquals(2, result.size.toLong())
    }

    @Test
    fun `Filter songs by contents`() {
        // given:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, false).apply()

        // when:
        val result = songService.filterSongs(Type.SONG.name, "foo", songs!!)

        // then:
        assertEquals(2, result.size.toLong())
    }

    @Test
    fun `Is search by song book number`() {
        // setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply()

        // expect:
        assertTrue(songService.isSearchBySongBookNumber(Type.SONG_BOOK.name, "2"))
    }

    @Test
    fun `Is search by song book number when string query`() {
        // setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true).apply()

        // expect:
        assertFalse(songService.isSearchBySongBookNumber(Type.SONG_BOOK.name, "que"))
    }

    fun `Is search by song book number when search by content`() {
        // setup:
        preferences.edit().putBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, false).apply()

        // expect:
        assertFalse(songService.isSearchBySongBookNumber(Type.SONG_BOOK.name, "2"))
    }

    fun `Get song book number`() {
        // setup:
        val result = songService.getSongBookNumber("98")

        // expect:
        assertEquals(98, result)
    }

    fun `Get default song book number`() {
        // setup:
        val result = songService.getSongBookNumber("quer")

        // expect:
        assertEquals(-1, result)
    }

    fun `Songs sort by song book number`() {
        // setup:
        val result = songService.getSortedSongs(Type.SONG_BOOK.name, HashSet<Song>(songs))

        // expect:
        assertEquals(2, result[0].songBookNumber)
        assertEquals(34, result[1].songBookNumber)
    }

    fun `Songs sort by title`() {
        // setup:
        val result = songService.getSortedSongs(Type.SONG.name, HashSet<Song>(songs))

        // expect:
        assertEquals("bar", result[0].title)
        assertEquals("foo", result[1].title)
    }

    fun `Filter service songs by empty query`() {
        // setup:
        val result = songService.filteredServiceSongs("", ArrayList<ServiceSong>())

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Filter service songs by unknown query`() {
        // setup:
        val serviceSongs = ArrayList<ServiceSong>()
        val song = Song()
        song.searchTitle = "foo @ bar"
        serviceSongs.add(ServiceSong("", song))
        val result = songService.filteredServiceSongs("query", serviceSongs)

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Filter service songs`() {
        // given:
        val serviceSongs = ArrayList<ServiceSong>()
        val song1 = Song()
        song1.searchTitle = "foo @ bar"

        val song2 = Song()
        song2.comments = "bar @bar"
        serviceSongs.add(ServiceSong("", song1))
        serviceSongs.add(ServiceSong("", song2))

        // when:
        val result = songService.filteredServiceSongs("fo", serviceSongs)

        // then:
        assertFalse(result.isEmpty())
    }

    fun `Filter multiple service songs`() {
        // given:
        val serviceSongs = ArrayList<ServiceSong>()
        val song1 = Song()
        song1.searchTitle = "foo @ bar"

        val song2 = Song()
        song2.comments = "bar @bar"
        serviceSongs.add(ServiceSong("", song1))
        serviceSongs.add(ServiceSong("", song2))

        // when:
        val result = songService.filteredServiceSongs("bar", serviceSongs)

        // then:
        assertFalse(result.isEmpty())
        assertEquals(2, result.size)
    }

    fun `Filter service songs when  list is null`() {
        // setup:
        val result = songService.filteredServiceSongs("fo", null)

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Get search titles from null`() {
        // setup:
        val result = songService.getSearchTitles(null)

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Get search titles from null song`() {
        // setup:
        val result = songService.getSearchTitles(ServiceSong("", null))

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Get search titles from empty search title`() {
        // setup:
        val song = Song()
        song.searchTitle = ""
        val result = songService.getSearchTitles(ServiceSong("", song))

        // expect:
        assertTrue(result.isEmpty())
    }

    fun `Get search titles`() {
        // setup:
        val song = Song()
        song.searchTitle = "foo @ bar"
        val result = songService.getSearchTitles(ServiceSong("", song))

        // expect:
        assertFalse(result.isEmpty())
    }

}