package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class FavouriteTest extends Specification
{
    def favourite1
    def favourite2

    def setup()
    {
        favourite1 = new Favourite(name: "foo", orderId: 1)
        favourite2 = new Favourite(name: favourite1.getName(), orderId: 2)
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
        def result = Favourite["Companion"].toJson(list)

        then:
        result == "[{\"orderId\":0,\"name\":\"foo\",\"dragDrops\":[]}]"
    }

    def "To arrays"()
    {
        given:
        def jsonString = "[{\"name\":\"foo\",\"dragDrops\":[]}]"

        when:
        def result = Favourite["Companion"].toArrays(jsonString)

        then:
        result.size() == 1
        def expected = new ArrayList<Favourite>()
        expected.add(new Favourite("foo", new ArrayList<DragDrop>()))
        result == expected
    }

    def "To sort order"()
    {
        setup:
        List<Favourite> favorites = new ArrayList<>()
        favorites.add(favourite1)
        favorites.add(favourite2)
        favorites.add(new Favourite(orderId: 3, name: "latest favourite"))
        Collections.sort(favorites)

        expect:
        favorites.get(0).getName() == "latest favourite"
    }

}
