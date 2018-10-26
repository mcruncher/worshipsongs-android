package org.worshipsongs.service

import android.content.SharedPreferences
import android.preference.PreferenceManager
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.Favourite
import org.worshipsongs.domain.SongDragDrop

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */
class FavouriteServiceTest extends ElectricSpecification
{
    def favouriteService = new FavouriteService(RuntimeEnvironment.application.getApplicationContext());
    def songService =  new SongService(RuntimeEnvironment.application.getApplicationContext())

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext())

    def setup()
    {
       favouriteService.setSongService(songService)
       favouriteService.setSharedPreferences(preferences)
    }

    def cleanup()
    {
        preferences.edit().remove(CommonConstants.FAVOURITES_KEY).apply()
    }

    def "Save"()
    {
        given:
        def songDragDrop = new SongDragDrop(0, "foo", false)
        songDragDrop.setTamilTitle("bar")

        when:
        favouriteService.save("service1", songDragDrop)

        then:
        favouriteService.find("service1").name == "service1"
    }

    def "Add song to existing favourite"()
    {
        given:
        def songDragDrop = new SongDragDrop(0, "foo", false)
        songDragDrop.setTamilTitle("bar")
        favouriteService.save("service1", songDragDrop)

        def songDragDrop2 = new SongDragDrop(2, "foo1", false)

        when:
        favouriteService.save("service1", songDragDrop2)

        then:
        def result = favouriteService.find("service1")
        result.dragDrops.size() == 2
    }

    def "Save  songs"()
    {
        given:
        def songDragDropList = new ArrayList<SongDragDrop>()
        def songDragDrop = new SongDragDrop(0, "foo", false)
        songDragDrop.setTamilTitle("bar")
        songDragDropList.add(songDragDrop)

        def songDragDrop2 = new SongDragDrop(1, "foo1", false)
        songDragDropList.add(songDragDrop2)

        when:
        favouriteService.save("service1", songDragDropList)

        then:
        def result = favouriteService.find("service1")
        result.dragDrops.size() == 2

    }

    def "Find"()
    {
        given:
        def songDragDrop = new SongDragDrop(0, "foo1", false)
        songDragDrop.setTamilTitle("bar1")

        when:
        favouriteService.save("service1", songDragDrop)

        then:
        favouriteService.find("service1").name == "service1"


    }

    def "FindNames"()
    {
        given:
        def songDragDropList = new ArrayList<SongDragDrop>()
        def songDragDrop = new SongDragDrop(0, "foo", false)
        songDragDrop.setTamilTitle("bar")
        songDragDropList.add(songDragDrop)

        def songDragDrop2 = new SongDragDrop(1, "foo1", false)
        songDragDropList.add(songDragDrop2)
        favouriteService.save("service1", songDragDropList)

        when:
        def result = favouriteService.findNames()

        then:
        result.size() == 1
        result.get(0) == "service1"
    }

    def "Build share favourite format"()
    {
        given: "favourite \"service1\" exists with two songs"
        def songDragDropList = new ArrayList<SongDragDrop>()
        def songDragDrop = new SongDragDrop(1, "foo", false)
        songDragDrop.setTamilTitle("bar")
        songDragDropList.add(songDragDrop)

        def songDragDrop2 = new SongDragDrop(2, "foo1", false)
        songDragDropList.add(songDragDrop2)
        favouriteService.save("service1", songDragDropList)

        when: "build share favourite format "
        def result = favouriteService.buildShareFavouriteFormat("service1")

        then:
        result.contains("foo")
        result.contains("bar")
        result.contains("https://worshipsongs.org/c2VydmljZTE7MTsyOw==")
    }

    def "Remove"()
    {
        given:
        def songDragDrop = new SongDragDrop(1, "foo", false)
        songDragDrop.setTamilTitle("bar")
        favouriteService.save("service1", songDragDrop)

        when:
        favouriteService.remove("service1")

        then:
        favouriteService.find("service1").name == null
    }

    def "RemoveSong"()
    {
        given:

        def songDragDropList = new ArrayList<SongDragDrop>()
        def songDragDrop = new SongDragDrop(0, "foo", false)
        songDragDrop.setTamilTitle("bar")
        songDragDropList.add(songDragDrop)

        def songDragDrop2 = new SongDragDrop(1, "foo1", false)
        songDragDropList.add(songDragDrop2)
        favouriteService.save("service1", songDragDropList)

        when:
        favouriteService.removeSong("service1", "foo")

        then:
        def result = favouriteService.find("service1")
        result.getDragDrops().size() == 1
        result.getDragDrops().get(0).title == "foo1"

    }

    def "Get new order number"()
    {
        setup:
        List<Favourite> favorites = new ArrayList<>()
        favorites.add(new Favourite(orderId: 1, name: "first favourite"))
        favorites.add(new Favourite(orderId: 3, name: "latest favourite"))
        favorites.add(new Favourite(orderId: 2, name: "second favourite"))

        expect:
        favouriteService.getFavouritesNewOrderNumber(favorites) == 4
    }
}
