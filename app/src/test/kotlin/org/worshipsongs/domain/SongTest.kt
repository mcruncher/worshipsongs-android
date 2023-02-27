package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongTest {
    lateinit var song1: Song;
    lateinit var song2: Song;

    @Before
    fun setup()
    {
        song1 = Song ("foo")
        song2 = Song (song1.title!!)
    }

    @Test
    fun `To string`()
    {
        // setup:
        val result = song1.toString()

        // expect:
        result.contains("foo")
        !result.contains("Song1")
    }

    @Test
    fun `Equals`()
    {
        // expect:
        assertEquals(song1, song2)
    }

    @Test
    fun `Not equals`()
    {
        // setup:
        song2.title = "bar"

        // expect:
        assertNotEquals(song1, song2)
    }

    @Test
    fun `Hashcode`()
    {
        // setup:
        val set = setOf<Song>(song1, song2)

        // expect:
        assertEquals(1, set.size)
    }
}
