package org.worshipsongs.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SettingTest {
    lateinit var setting1: Setting;
    lateinit var setting2: Setting;

    @Before
    fun setup() {
        setting1 = Setting.instance
        setting1.position = 1
        setting2 = Setting.instance
    }

    @Test
    fun `To string`() {
        // setup:
        val result = setting1.toString()

        // expect:
        assertTrue(result.contains("1"))
    }

    @Test
    fun `Equals`() {
        // expect:
        assertEquals(setting1, setting2)
    }

    @Test
    fun `HashCode`() {
        // setup:
        val set = setOf<Setting>(setting1, setting2)

        // expect:
        assertEquals(1, set.size)
    }
}
