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
    fun testToString() {
        // setup:
        val result = setting1.toString()

        // expect:
        assertTrue(result.contains("1"))
    }

    @Test
    fun testEquals() {
        // expect:
        assertEquals(setting1, setting2)
    }

    @Test
    fun testHashCode() {
        // setup:
        val set = setOf(setting1, setting2)

        // expect:
        assertEquals(1, set.size)
    }
}
