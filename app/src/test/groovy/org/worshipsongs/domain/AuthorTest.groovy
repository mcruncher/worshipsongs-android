package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : x.x.x
 */
class AuthorTest extends Specification
{
    def author1


    void setup()
    {
        author1 = new Author()
        author1.setFirstName("foo")
    }

    def "ToString"()
    {
        setup:
        def result = author1.toString()

        expect:
        result.contains("foo")

    }

}
