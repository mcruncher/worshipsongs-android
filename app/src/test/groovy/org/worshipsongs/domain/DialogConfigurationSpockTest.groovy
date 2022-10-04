package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class DialogConfigurationSpockTest extends Specification
{
    def dialogConfiguration1
    def dialogConfiguration2

    def setup()
    {
        dialogConfiguration1 = new DialogConfiguration("foo", "foomessage")
        dialogConfiguration2 = new DialogConfiguration(dialogConfiguration1.getTitle(), dialogConfiguration1.getMessage())
    }

    def "To string"()
    {
        setup:
        def result = dialogConfiguration1.toString()

        expect:
        result.contains("foo")
        !result.contains("bar")
    }

    def "Equals"()
    {
        expect:
        dialogConfiguration1.equals(dialogConfiguration2)
    }

    def "Not equals"()
    {
        setup:
        dialogConfiguration2.setTitle("bar")

        expect:
        !dialogConfiguration1.equals(dialogConfiguration2)
    }

    def "Hashcode"()
    {
        setup:
        def set = new HashSet<DialogConfiguration>()
        set.add(dialogConfiguration1)
        set.add(dialogConfiguration2)

        expect:
        set.size() == 1

    }
}
