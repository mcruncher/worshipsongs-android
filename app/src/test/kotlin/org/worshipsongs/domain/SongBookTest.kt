package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongBookTest
{
    lateinit var songBook1: SongBook
    lateinit var songBook2: SongBook

    @Before
    fun setup()
    {
        songBook1 = SongBook("foo")
        songBook2 = SongBook(songBook1.name)
    }

    @Test
    fun testToString() {
        // setup:
        val result = songBook1.toString()

        // expect:
        assertTrue(result.contains("foo"))
        assertFalse(result.contains("bar"))
    }

    @Test
    fun testEquals() {
        // expect:
        assertEquals(songBook1, songBook2)
    }

    @Test
    fun testNotEquals() {
        // setup:
        songBook2.name = "bar"

        // expect:
        !songBook1.equals(songBook2)
    }

    @Test
    fun testHashCode() {
        // setup:
        val songBooks = setOf(songBook1, songBook2)

        // expect:
        assertEquals(1, songBooks.size)
    }
}
