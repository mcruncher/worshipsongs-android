package org.worshipsongs.domain

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class ServiceSongTest {

    @Test
    fun testToString() {
        // given:
        val serviceSong = ServiceSong("Foo", Song())

        // when:
        val result = serviceSong.toString()

        // then:
        assertTrue(result.contains("Foo"))
    }
}
