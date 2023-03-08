package org.worshipsongs.service


import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.worshipsongs.domain.Author
import java.util.List

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner::class)
class AuthorServiceTest {
    val authorService = AuthorService(ApplicationProvider.getApplicationContext())
    val authors = List.of(Author("foo author 1"), Author("foo author 2"), Author("bar author 1"))

    @Test
    fun `Get authors when search text is empty`() {
        // given: "some authors exist"

        // and: "the search text is empty"
        val searchText = ""

        // when: "searching"
        val result = authorService.getAuthors(searchText, authors)

        // then: "all the authors should be fetched"
        assertEquals(3, result.size)
    }

    @Test
    fun `Get authors when the search text does not match any author`() {
        // given: "some authors exist"

        // and: "the search text doesn't match any author"
        val searchText = "sometext"

        // when: "searching"
        val result = authorService.getAuthors(searchText, authors)

        // then: "the search result should be empty"
        assertEquals(0, result.size)
    }

    @Test
    fun `Get authors when the search text matches some authors`() {
        // given: "some authors exist"

        // and: "the search text matches some authors"
        val searchText = "foo"

        // when: "searching"
        val result = authorService.getAuthors(searchText, authors)

        // then: "the authors matching the search text should be fetched"
        assertEquals(2, result.size)
    }
}
