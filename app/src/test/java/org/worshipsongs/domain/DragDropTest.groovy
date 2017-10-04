package org.worshipsongs.domain

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x.x
 */
class DragDropTest extends Specification
{
    def dragDrop1 = null
    def dragDrop2 = null

    void setup()
    {
        dragDrop1 = new DragDrop(0, "Foo", false)
        dragDrop2 = new DragDrop(dragDrop1.id, dragDrop1.title, dragDrop1.checked)
    }

    def "ToString"()
    {
        setup:
        def result = dragDrop1.toString()

        expect:
        result.contains("Foo")
        !result.contains("bar")
    }

    def "Equals"()
    {
        expect:
        dragDrop1.equals(dragDrop2)
    }

    def "Not equals"()
    {
        setup:
        dragDrop1.title = "foo1"

        expect:
        !dragDrop1.equals(dragDrop2)
    }

    def "HashCode"()
    {
        setup:
        def set = new HashSet<DragDrop>()
        set.add(dragDrop1)
        set.add(dragDrop2)

        expect:
        set.size() == 1
    }

    def "To gson"()
    {
        setup:
        def dragDrops = new ArrayList<DragDrop>()
        dragDrops.add(new DragDrop(0, "title", true))
        def jsonString = DragDrop.toJson(dragDrops)

        expect:
        jsonString == "[{\"id\":0,\"title\":\"title\",\"checked\":true}]"
    }

    def "To array"()
    {
        setup:
        def jsonString = "[{\"id\":0,\"title\":\"title\",\"checked\":true}]"
        def result = DragDrop.toArrays(jsonString)
        def dragDrop = new DragDrop(0, "title", true)

        expect:
        dragDrop == result[0]
    }

    def "To array when empty string"()
    {
        setup:
        def result = DragDrop.toArrays("")

        expect:
        result.size() == 0
    }
}
