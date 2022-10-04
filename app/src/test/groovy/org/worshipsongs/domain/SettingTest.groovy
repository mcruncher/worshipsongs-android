package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SettingTest extends Specification
{
    def setting1;
    def setting2;

    void setup()
    {
        setting1 = Setting["Companion"].instance
        setting1.setPosition(1)
        setting2 = Setting["Companion"].instance
    }

    def "ToString"()
    {
        setup:
        def result = setting1.toString()

        expect:
        result.contains("1")
    }

    def "Equals"()
    {
        expect:
        setting1.equals(setting2)
    }

    def "HashCode"()
    {
        setup:
        def set = new HashSet()
        set.add(setting1)
        set.add(setting2)

        expect:
        set.size() == 1
    }
}
