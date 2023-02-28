package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class DialogConfigurationTest {
    lateinit var dialogConfiguration1: DialogConfiguration
    lateinit var dialogConfiguration2: DialogConfiguration

    @Before
    fun setup() {
        dialogConfiguration1 = DialogConfiguration("foo", "foomessage")
        dialogConfiguration2 =
            DialogConfiguration(dialogConfiguration1.title!!, dialogConfiguration1.message)
    }

    @Test
    fun testToString() {
        // setup:
        val result = dialogConfiguration1.toString()

        // expect:
        assertTrue(result.contains("foo"))
        assertFalse(result.contains("bar"))
    }

    @Test
    fun testEquals() {
        // expect:
        assertEquals(dialogConfiguration1, dialogConfiguration2)
    }

    @Test
    fun testNotEquals() {
        // setup:
        dialogConfiguration2.title = "bar"

        // expect:
        assertNotEquals(dialogConfiguration1, dialogConfiguration2)
    }

    @Test
    fun testHashcode() {
        // setup:Ø
        val set = setOf(dialogConfiguration1, dialogConfiguration2)

        // expect:
        assertEquals(1, set.size)
    }
}
