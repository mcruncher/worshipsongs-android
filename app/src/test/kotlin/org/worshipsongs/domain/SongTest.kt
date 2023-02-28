package org.worshipsongs.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

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
    fun testToString() {
        // setup:
        val result = song1.toString()

        // expect:
        result.contains("foo")
        !result.contains("Song1")
    }

    @Test
    fun testEquals() {
        // expect:
        assertEquals(song1, song2)
    }

    @Test
    fun testNotEquals() {
        // setup:
        song2.title = "bar"

        // expect:
        assertNotEquals(song1, song2)
    }

    @Test
    fun testHashcode() {
        // setup:
        val set = setOf(song1, song2)

        // expect:
        assertEquals(1, set.size)
    }
}
