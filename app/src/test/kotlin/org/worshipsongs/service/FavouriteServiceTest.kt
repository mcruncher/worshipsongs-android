package org.worshipsongs.service

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.Favourite
import org.worshipsongs.domain.SongDragDrop

/**
 * Author : Madasamy
 * Version : 3.x.x
 */
@RunWith(RobolectricTestRunner::class)
class FavouriteServiceTest {
    val favouriteService = FavouriteService(ApplicationProvider.getApplicationContext())
    val songService = SongService(ApplicationProvider.getApplicationContext())
    var preferences =
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    @Before
    fun setup() {
        favouriteService.setSongService(songService)
        favouriteService.setSharedPreferences(preferences)
    }

    @After
    fun cleanup() {
        preferences.edit().remove(CommonConstants.FAVOURITES_KEY).apply()
    }

    @Test
    fun save() {
        // given:
        val songDragDrop = SongDragDrop(0, "foo", false)
        songDragDrop.tamilTitle = "bar"

        // when:
        favouriteService.save("service1", songDragDrop)

        // then:
        Assert.assertEquals("service1", favouriteService.find("service1").name)
    }

    @Test
    fun `Add song to existing favourite`() {
        // given:
        val songDragDrop = SongDragDrop(0, "foo", false)
        songDragDrop.tamilTitle = "bar"
        favouriteService.save("service1", songDragDrop)
        val songDragDrop2 = SongDragDrop(2, "foo1", false)

        // when:
        favouriteService.save("service1", songDragDrop2)

        // then:
        val result = favouriteService.find("service1")
        Assert.assertEquals(2, result.dragDrops!!.size.toLong())
    }

    @Test
    fun `Save songs`() {
        // given:
        val songDragDrop1 = SongDragDrop(0, "foo", false)
        songDragDrop1.tamilTitle = "bar"
        val songDragDrop2 = SongDragDrop(1, "foo1", false)
        val songDragDrops = java.util.List.of(songDragDrop1, songDragDrop2)

        // when:
        favouriteService.save("service1", songDragDrops)

        // then:
        val result = favouriteService.find("service1")
        Assert.assertEquals(2, result.dragDrops!!.size.toLong())
    }

    @Test
    fun find() {
        // given:
        val songDragDrop = SongDragDrop(0, "foo1", false)
        songDragDrop.tamilTitle = "bar1"

        // when:
        favouriteService.save("service1", songDragDrop)

        // then:
        Assert.assertEquals("service1", favouriteService.find("service1").name)
    }

    @Test
    fun `Find names`() {
        // given:
        val songDragDrop1 = SongDragDrop(0, "foo", false)
        songDragDrop1.tamilTitle = "bar"
        val songDragDrop2 = SongDragDrop(1, "foo1", false)
        val songDragDrops = java.util.List.of(songDragDrop1, songDragDrop2)
        favouriteService.save("service1", songDragDrops)

        // when:
        val result: List<String> = favouriteService.findNames()

        // then:
        Assert.assertEquals(1, result.size.toLong())
        Assert.assertEquals("service1", result[0])
    }

    @Test
    fun `Build share favourite format`() {
        // given: "favourite \"service1\" exists with two songs"
        val songDragDrop1 = SongDragDrop(1, "foo", false)
        songDragDrop1.tamilTitle = "bar"
        val songDragDrop2 = SongDragDrop(2, "foo1", false)
        val songDragDrops = java.util.List.of(songDragDrop1, songDragDrop2)
        favouriteService.save("service1", songDragDrops)

        // when: "build share favourite format "
        val result = favouriteService.buildShareFavouriteFormat("service1")

        // then:
        Assert.assertTrue(result.contains("foo"))
        Assert.assertTrue(result.contains("bar"))
        Assert.assertTrue(result.contains("https://mcruncher.github.io/worshipsongs/?c2VydmljZTE7MTsyOw=="))
    }

    @Test
    fun remove() {
        // given:
        val songDragDrop = SongDragDrop(1, "foo", false)
        songDragDrop.tamilTitle = "bar"
        favouriteService.save("service1", songDragDrop)

        // when:
        favouriteService.remove("service1")

        // then:
        Assert.assertNull(favouriteService.find("service1").name)
    }

    @Test
    fun `Remove song`() {
        // given:
        val songDragDrop1 = SongDragDrop(0, "foo", false)
        songDragDrop1.tamilTitle = "bar"
        val songDragDrop2 = SongDragDrop(1, "foo1", false)
        val songDragDrops = java.util.List.of(songDragDrop1, songDragDrop2)
        favouriteService.save("service1", songDragDrops)

        // when:
        favouriteService.removeSong("service1", "foo")

        // then:
        val result = favouriteService.find("service1")
        Assert.assertEquals(1, result.dragDrops!!.size.toLong())
        Assert.assertEquals("foo1", result.dragDrops!![0].title)
    }

    @Test
    fun `Get new order number`() {
        // setup:
        val favorites: MutableList<Favourite> = ArrayList()
        favorites.add(Favourite(1, "first favourite", ArrayList()))
        favorites.add(Favourite(3, "latest favourite", ArrayList()))
        favorites.add(Favourite(2, "second favourite", ArrayList()))

        // expect:
        Assert.assertEquals(4, favouriteService.getFavouritesNewOrderNumber(favorites).toLong())
    }

}