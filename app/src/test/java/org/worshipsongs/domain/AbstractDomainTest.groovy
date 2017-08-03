package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class AbstractDomainTest extends Specification
{
    def abstractDomain1;
    def abstractDomain2;

    def setup()
    {
        abstractDomain1 = new AbstractDomain()
        abstractDomain1.setName("foo")

        abstractDomain2 = new AbstractDomain()
        abstractDomain2.setName(abstractDomain1.getName())
    }

    def "To String"()
    {
        setup:
        def result = abstractDomain1.toString()

        expect:
        result.contains("foo")
    }

    def "Equals"()
    {
        expect:
        abstractDomain1.equals(abstractDomain2)
    }

    def "Not equals"()
    {
        setup:
        abstractDomain2.setName("bar")

        expect:
        !abstractDomain2.equals(abstractDomain1)
    }

    def "Hash code"()
    {
        setup:
        def set = new HashSet()
        set.add(abstractDomain1)
        set.add(abstractDomain2)

        expect:
        set.size() == 1

    }

}
