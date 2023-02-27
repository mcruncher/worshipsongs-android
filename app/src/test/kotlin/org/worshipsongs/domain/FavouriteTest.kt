package org.worshipsongs.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class FavouriteTest {
    lateinit var favourite1: Favourite
    lateinit var favourite2: Favourite

    @Before
    fun setup() {
        favourite1 = Favourite(1, "foo", ArrayList())
        favourite2 = Favourite(1, favourite1.name!!, ArrayList())
    }

    @Test
    fun `To string`() {
        // setup:
        val result = favourite1.toString()

        // expect:
        assertTrue(result.contains("foo"))
    }

    @Test
    fun `Equals`() {
        // expect:
        assertEquals(favourite1, favourite2)
    }

    @Test
    fun `Not equals`() {
        // setup:
        favourite2.name = "bar"

        // expect:
        assertNotEquals(favourite1, favourite2)
        assertNotEquals(favourite1, Song())
    }

    @Test
    fun `HashCode`() {
        // setup:
        val favourites = setOf(favourite1, favourite2, Favourite())

        // expect:
        assertEquals(2, favourites.size)
    }

    @Test
    fun `To json`() {
        // given:
        val list = listOf<Favourite>(Favourite("foo", ArrayList()))
        val expected = "[{\"orderId\":0,\"name\":\"foo\",\"dragDrops\":[]}]"

        // when:
        val result = Favourite.toJson(list)

        // then:
        assertEquals(expected, result)
    }

    @Test
    fun `To arrays`() {
        // given:
        val jsonString = "[{\"name\":\"foo\",\"dragDrops\":[]}]"

        // when:
        val result = Favourite.toArrays(jsonString)

        // then:
        val expected = ArrayList<Favourite>()
        expected.add(Favourite("foo", ArrayList()))
        assertEquals(1, result.size)
        assertEquals(expected, result)
    }

    @Test
    fun `To sort order`() {
        // setup:
        val favorites =
            listOf(favourite1, favourite2, Favourite(3, "latest favourite", ArrayList()))
        Collections.sort(favorites)

        // expect:
        assertEquals("latest favourite", favorites[0].name)
    }

}
