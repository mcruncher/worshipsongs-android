package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class SongDragDropTest {
    lateinit var songDragDrop1: SongDragDrop
    lateinit var songDragDrop2: SongDragDrop

    @Before
    fun setup() {
        songDragDrop1 = SongDragDrop(0, "foo", false)
        songDragDrop1.tamilTitle = "fooo"
        songDragDrop2 =
            SongDragDrop(songDragDrop1.id, songDragDrop1.title!!, songDragDrop1.isChecked)
    }

    @Test
    fun `To string`() {
        // setup:
        val result = songDragDrop1.toString()

        // expect:
        assertTrue(result.contains("foo"))
        assertFalse(result.contains("bar"))
    }

    @Test
    fun `Equals`() {
        // expect:
        assertEquals(songDragDrop1, songDragDrop2)
    }

    @Test
    fun `Not equals`() {
        // setup:
        songDragDrop1.title = "bar1"

        // expect:
        assertNotEquals(songDragDrop1, songDragDrop2)
    }

    @Test
    fun `Hashcode`() {
        // setup:
        val songDragDropSet = setOf(songDragDrop1, songDragDrop2, SongDragDrop())

        // expect:
        assertEquals(2, songDragDropSet.size)
    }

    @Test
    fun `To json`() {
        // given:
        val list = listOf(songDragDrop1)

        // when:
        val result = SongDragDrop.toJson(list)

        // then:
        val expected = "[{\"tamilTitle\":\"fooo\",\"id\":0,\"title\":\"foo\",\"isChecked\":false}]"
        assertEquals(expected, result)
    }

    @Test
    fun `To list`() {
        // given:
        val jsonString = "[{\"tamilTitle\":\"fooo\",\"id\":0,\"title\":\"foo\",\"isChecked\":true}]"

        // when:
        val result = SongDragDrop.toList(jsonString)

        // then:
        val expected = listOf(songDragDrop1)
        assertEquals(expected, result)
    }
}
