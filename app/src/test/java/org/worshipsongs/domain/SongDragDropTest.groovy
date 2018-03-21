package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class SongDragDropTest extends Specification
{
    def songDragDrop1 = null
    def songDragDrop2 = null

    def setup()
    {
        songDragDrop1 = new SongDragDrop(0, "foo", false)
        songDragDrop1.tamilTitle = "fooo"
        songDragDrop2 = new SongDragDrop(songDragDrop1.id, songDragDrop1.title, songDragDrop1.checked)

    }

    def "ToString"()
    {
        setup:
        def result = songDragDrop1.toString()

        expect:
        result.contains("foo")
        !result.contains("bar")
    }

    def "To json"()
    {
        given:
        def list = new ArrayList<SongDragDrop>()
        list.add(songDragDrop1)
        when:
        def result = SongDragDrop.toJson(list)

        then:
        result == "[{\"tamilTitle\":\"fooo\",\"id\":0,\"title\":\"foo\",\"checked\":false}]"
    }

    def "ToList"()
    {
        given:
        def jsonString = "[{\"tamilTitle\":\"fooo\",\"id\":0,\"title\":\"foo\",\"checked\":false}]"

        when:
        def result = SongDragDrop.toArrays(jsonString)

        then:
        def expected = new ArrayList<SongDragDrop>()
        expected.add(songDragDrop1)
        result == expected
    }
}
