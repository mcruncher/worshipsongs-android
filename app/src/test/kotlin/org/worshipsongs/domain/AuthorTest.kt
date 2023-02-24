package org.worshipsongs.domain

import org.junit.Before
import org.junit.Test

class AuthorTest {
    lateinit var author1: Author

    @Before
    fun setup()
    {
        author1 = Author()
        author1.firstName == "foo"
    }

    @Test
    fun testToString()
    {
        //setup
        val result = author1.toString()

        //expect
        result.contains("foo")
    }
}