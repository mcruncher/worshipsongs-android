package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Author : Madasamy
 * Version : x.x.x
 */
class TopicsTest {
    lateinit var topics1: Topics
    lateinit var topics2: Topics

    @Before
    fun setUp() {
        topics1 = Topics("foo")
        topics2 = Topics(topics1.name!!)
    }

    @Test
    fun testToString() {
        val result = topics1.toString()
        assertTrue(result.contains("foo"))
    }

    @Test
    fun testEquals() {
        assertEquals(topics1, topics2)
    }

    @Test
    fun testNotEquals() {
        topics1.name = "bar"
        assertNotEquals(topics1, topics2)
    }

    @Test
    fun testHashCode() {
        val topicsSet = setOf(topics1, topics2)
        assertEquals(1, topicsSet.size)
    }
}