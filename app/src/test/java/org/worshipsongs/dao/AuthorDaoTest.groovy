package org.worshipsongs.dao

import org.worshipsongs.utils.RegexUtils
import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class AuthorDaoTest extends Specification
{
    def authorDao = new AuthorDao();

    def "Parse tamil author name"()
    {
        given:
        def name = "Foo={இடைவிடா நன்றி உமக்குத்தான}"

        when:
        def result = authorDao.parseTamilAuthorName(name)

        then:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Parse tamil author name from default"()
    {
        given:
        def name = "Foo"

        when:
        def result = authorDao.parseTamilAuthorName(name)

        then:
        result == "Foo"
    }

    def "Parse tamil author name from null"()
    {
        setup:
        def result = authorDao.parseTamilAuthorName(null)

        expect:
        result == ""
    }

    def "Parse tamil author name from empty"()
    {
        setup:
        def result = authorDao.parseTamilAuthorName("")

        expect:
        result == ""
    }

    def "Parse default name"()
    {
        given:
        def name = "Foo{இடைவிடா நன்றி உமக்குத்தான}"

        when:
        def result = authorDao.parseDefaultName(name)

        then:
        result == "Foo"
    }

    def "Parse default name when tamil name not defined "()
    {
        given:
        def name = "Foo bar "

        when:
        def result = authorDao.parseDefaultName(name)

        then:
        result == "Foo bar "
    }

    def "Parse default author name from null"()
    {
        setup:
        def result = authorDao.parseDefaultName(null)

        expect:
        result == ""
    }

    def "Parse default author name from empty"()
    {
        setup:
        def result = authorDao.parseDefaultName("")

        expect:
        result == ""
    }

}
