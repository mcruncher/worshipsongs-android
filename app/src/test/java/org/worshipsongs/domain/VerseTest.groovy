package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class VerseTest extends Specification
{
    def "ToString"()
    {
        given:
        def verse = new Verse()
        verse.setLabel(1)
        verse.setContent("foo")
        verse.setType("L")

        when:
        def result = verse.toString()

        then:
        result.contains("foo")
    }
}
