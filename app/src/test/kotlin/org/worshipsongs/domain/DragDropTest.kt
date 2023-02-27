package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class DragDropTest {
    lateinit var dragDrop1: DragDrop
    lateinit var dragDrop2: DragDrop

    @Before
    fun setup() {
        dragDrop1 = DragDrop(0, "Foo", false)
        dragDrop2 = DragDrop(dragDrop1.id, dragDrop1.title!!, dragDrop1.isChecked)
    }

    @Test
    fun `ToString`() {
        // setup:
        val result = dragDrop1.toString()

        // expect:
        assertTrue(result.contains("Foo"))
        assertFalse(result.contains("bar"))
    }

    @Test
    fun `Equals`() {
        // expect:
        assertEquals(dragDrop1, dragDrop2)
    }

    @Test
    fun `Not equals`() {
        // setup:
        dragDrop1.title = "foo1"

        // expect:
        assertNotEquals(dragDrop1, dragDrop2)
    }

    @Test
    fun `HashCode`() {
        // setup:
        val set = setOf(dragDrop1, dragDrop2)

        // expect:
        assertEquals(1, set.size)
    }

    @Test
    fun `To json`() {
        // setup:
        val dragDrops = listOf<DragDrop>(DragDrop(0, "title", true))
        val expected = "[{\"id\":0,\"title\":\"title\",\"isChecked\":true}]"
        val result = DragDrop.toJson(dragDrops)

        // expect:
        assertEquals(expected, result)
    }

    @Test
    fun `To array`() {
        // setup:
        val jsonString = "[{\"id\":0,\"title\":\"title\",\"isChecked\":true}]"
        val result = DragDrop.toArrays(jsonString)
        val expected = DragDrop(0, "title", true)

        // expect:
        assertEquals(expected, result[0])
    }

    @Test
    fun `To array when empty string`() {
        // setup:
        val result = DragDrop.toArrays("")

        // expect:
        assertEquals(0, result.size)
    }
}
