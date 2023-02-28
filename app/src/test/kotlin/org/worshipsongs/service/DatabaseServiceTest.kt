package org.worshipsongs.service

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class DatabaseServiceTest {
    val databaseService = DatabaseService();

    @Test
    fun `Parse tamil topic name`() {
        // given:
        val name = "Foo={இடைவிடா நன்றி உமக்குத்தான}"

        // when:
        val result = databaseService.parseTamilName(name)

        // then:
        assertEquals("இடைவிடா நன்றி உமக்குத்தான", result)
    }

    @Test
    fun `Parse tamil topic name when not defined`() {
        // given:
        val name = "Foo"

        // when:
        val result = databaseService.parseTamilName(name)

        // then:
        assertEquals("Foo", result)
    }

    @Test
    fun `Parse tamil topic name from null`() {
        // setup:
        val result = databaseService.parseTamilName(null)

        // expect:
        assertEquals("", result)
    }

    @Test
    fun `Parse tamil topic name from empty`() {
        // setup:
        val result = databaseService.parseTamilName("")

        // expect:
        assertEquals("", result)
    }

    @Test
    fun `Parse default name`() {
        // given:
        val name = "Foo"

        // when:
        val result = databaseService.parseTamilName(name)

        // then:
        assertEquals("Foo", result)
    }

    @Test
    fun `Parse default name when tamil name not defined `() {
        // given:
        val name = "Foo bar "

        // when:
        val result = databaseService.parseEnglishName(name)

        // then:
        assertEquals("Foo bar ", result)
    }

    @Test
    fun `Parse default topic name from null`() {
        // setup:
        val result = databaseService.parseEnglishName(null)

        // expect:
        assertEquals("", result)
    }

    @Test
    fun `Parse default topic name from empty`() {
        // setup:
        val result = databaseService.parseEnglishName(null)

        // expect:
        assertEquals("", result)
    }
}
