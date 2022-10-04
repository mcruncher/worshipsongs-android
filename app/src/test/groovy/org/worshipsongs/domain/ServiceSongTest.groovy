package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class ServiceSongTest extends Specification
{

    def "ToString"()
    {
        given:
        def serviceSong = new ServiceSong("Foo", new Song())

        when:
        def result = serviceSong.toString()

        then:
        result.contains("Foo")
    }
}
