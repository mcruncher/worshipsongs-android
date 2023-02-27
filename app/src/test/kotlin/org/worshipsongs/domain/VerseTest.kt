package org.worshipsongs.domain

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class VerseTest
{
    @Test
    fun `To string`() {
        // given:
        val verse = Verse()
        verse.label = 1
        verse.content = "foo"
        verse.type = "L"

        // when:
        val result = verse.toString()

        // then:
        assertTrue(result.contains("foo"))
    }
}
