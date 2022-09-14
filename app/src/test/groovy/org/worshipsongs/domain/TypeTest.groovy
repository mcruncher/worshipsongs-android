package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class TypeTest extends Specification
{
    def "enums"()
    {
        expect:
        Type.values().size() == 4
        Type.valueOf("SONG") == Type.SONG
    }
}
