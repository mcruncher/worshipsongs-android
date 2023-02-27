package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class DialogConfigurationSpockTest {
    lateinit var dialogConfiguration1: DialogConfiguration
    lateinit var dialogConfiguration2: DialogConfiguration

    @Before
    fun setup() {
        dialogConfiguration1 = DialogConfiguration("foo", "foomessage")
        dialogConfiguration2 =
            DialogConfiguration(dialogConfiguration1.title!!, dialogConfiguration1.message)
    }

    @Test
    fun `To string`() {
        // setup:
        val result = dialogConfiguration1.toString()

        // expect:
        assertTrue(result.contains("foo"))
        assertFalse(result.contains("bar"))
    }

    @Test
    fun `Equals`() {
        // expect:
        assertEquals(dialogConfiguration1, dialogConfiguration2)
    }

    @Test
    fun `Not equals`() {
        // setup:
        dialogConfiguration2.title = "bar"

        // expect:
        assertNotEquals(dialogConfiguration1, dialogConfiguration2)
    }

    @Test
    fun `Hashcode`() {
        // setup:Ø
        val set = setOf(dialogConfiguration1, dialogConfiguration2)

        // expect:
        assertEquals(1, set.size)
    }
}
