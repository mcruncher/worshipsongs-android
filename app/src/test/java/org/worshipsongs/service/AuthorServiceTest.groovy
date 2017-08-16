package org.worshipsongs.service

import hkhc.electricspock.ElectricSpecification
import org.worshipsongs.domain.Author
import org.worshipsongs.utils.RegexUtils

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class AuthorServiceTest extends ElectricSpecification
{
    def authorService = new AuthorService()

    def "Get authors when search by null"()
    {
        given:
        def authorList = new ArrayList<Author>()
        authorList.add(new Author("foo"))

        when:
        def result = authorService.getAuthors(null, authorList)

        then:
        result.size() == 1

    }

    def "Get authors when search by empty"()
    {
        given:
        def authorList = new ArrayList<Author>()
        authorList.add(new Author("foo"))

        when:
        def result = authorService.getAuthors("", authorList)

        then:
        result.size() == 1
    }

    def "Get authors when search by invalid text"()
    {
        given:
        def authorList = new ArrayList<Author>()
        authorList.add(new Author("foo"))

        when:
        def result = authorService.getAuthors("bar", authorList)

        then:
        result.size() == 1
    }

    def "Get authors when search by valid text"()
    {
        given:
        def authorList = new ArrayList<Author>()
        authorList.add(new Author("foo"))

        when:
        def result = authorService.getAuthors("fo", authorList)

        then:
        result.size() == 1
    }
}