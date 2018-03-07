package org.worshipsongs.domain

import hkhc.electricspock.ElectricSpecification

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class FavouriteTest extends ElectricSpecification
{
    def favourite1 = new Favourite()
    def favourite2 = new Favourite()

    def setup()
    {
        favourite1.setName("foo")
        favourite2.setName(favourite1.getName())
    }

    def "ToString"()
    {
        setup:
        def result = favourite1.toString()

        expect:
        result.contains("foo")
    }

    def "Equals"()
    {
        expect:
        favourite1.equals(favourite2)
    }

    def "Not equals"()
    {
        setup:
        favourite2.setName("bar")

        expect:
        !favourite1.equals(favourite2)
        !favourite1.equals(new Song())
    }

    def "HashCode"()
    {
        setup:
        def favourites = new HashSet<Favourite>()
        favourites.add(favourite1)
        favourites.add(favourite2)
        favourites.add(new Favourite())

        expect:
        favourites.size() == 2
    }

    def "To json"()
    {
        given:
        def list = new ArrayList<Favourite>()
        list.add(new Favourite("foo", new ArrayList<DragDrop>()))

        when:
        def result = Favourite.toJson(list)

        then:
        result == "[{\"name\":\"foo\",\"dragDrops\":[]}]"
    }

    def "To arrays"()
    {
        given:
        def jsonString = "[{\"name\":\"foo\",\"dragDrops\":[]}]"

        when:
        def result = Favourite.toArrays(jsonString)

        then:
        result.size() == 1
        def expected = new ArrayList<Favourite>()
        expected.add(new Favourite("foo", new ArrayList<DragDrop>()))
        result == expected
    }
}
