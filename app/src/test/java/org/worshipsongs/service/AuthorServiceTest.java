package org.worshipsongs.service;


import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.worshipsongs.domain.Author;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner.class)
public class AuthorServiceTest {
    private final AuthorService authorService = new AuthorService(ApplicationProvider.getApplicationContext());
    List<Author> authors = List.of(new Author("foo author 1"), new Author("foo author 2"), new Author("bar author 1"));

    @Test
    public void getAuthorsWhenSearchTextIsEmpty() {
        // given: "some authors exist"

        // and: "the search text is empty"
        String searchText = "";

        // when: "searching"
        List<Author> result = authorService.getAuthors(searchText, authors);

        // then: "all the authors should be fetched"
        assertEquals(3, result.size());
    }

    @Test
    public void getAuthorsWhenTheSearchTextDoesNotMatchAnyAuthor() {
        // given: "some authors exist"

        // and: "the search text doesn't match any author"
        String searchText = "sometext";

        // when: "searching"
        List<Author> result = authorService.getAuthors(searchText, authors);

        // then: "the search result should be empty"
        assertEquals(0, result.size());
    }

    @Test
    public void getAuthorsWhenTheSearchTextMatchesSomeAuthors() {
        // given: "some authors exist"

        // and: "the search text matches some authors"
        String searchText = "foo";

        // when: "searching"
        List<Author> result = authorService.getAuthors(searchText, authors);

        // then: "the authors matching the search text should be fetched"
        assertEquals(2, result.size());
    }
}
