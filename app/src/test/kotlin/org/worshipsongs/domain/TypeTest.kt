package org.worshipsongs.domain

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class TypeTest {

    @Test
    fun enums() {
        // expect:
        assertEquals(4, Type.values().size)
        assertEquals(Type.SONG, Type.valueOf("SONG"))
    }
}
