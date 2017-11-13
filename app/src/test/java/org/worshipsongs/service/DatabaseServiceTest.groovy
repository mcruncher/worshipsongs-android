package org.worshipsongs.service

import org.worshipsongs.service.DatabaseService
import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class DatabaseServiceTest extends Specification
{
    def databaseService = new DatabaseService();

    def "Parse tamil topic name"()
    {
        given:
        def name = "Foo={இடைவிடா நன்றி உமக்குத்தான}"

        when:
        def result = databaseService.parseTamilName(name)

        then:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Parse tamil topic name from default"()
    {
        given:
        def name = "Foo"

        when:
        def result = databaseService.parseTamilName(name)

        then:
        result == "Foo"
    }

    def "Parse tamil topic name from null"()
    {
        setup:
        def result = databaseService.parseTamilName(null)

        expect:
        result == ""
    }

    def "Parse tamil topic name from empty"()
    {
        setup:
        def result = databaseService.parseTamilName("")

        expect:
        result == ""
    }

    def "Parse default name"()
    {
        given:
        def name = "Foo"

        when:
        def result = databaseService.parseTamilName(name)

        then:
        result == "Foo"
    }

    def "Parse default name when tamil name not defined "()
    {
        given:
        def name = "Foo bar "

        when:
        def result = databaseService.parseEnglishName(name)

        then:
        result == "Foo bar "
    }

    def "Parse default topic name from null"()
    {
        setup:
        def result = databaseService.parseEnglishName(null)

        expect:
        result == ""
    }

    def "Parse default topic name from empty"()
    {
        setup:
        def result = databaseService.parseEnglishName(null)

        expect:
        result == ""
    }
}
