package org.worshipsongs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.domain.Favourite;
import org.worshipsongs.domain.SongDragDrop;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

@RunWith(RobolectricTestRunner.class)
public class FavouriteServiceTest {
    private FavouriteService favouriteService = new FavouriteService(ApplicationProvider.getApplicationContext());
    private SongService songService = new SongService(ApplicationProvider.getApplicationContext());

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

    @Before
    public void setup() {
        favouriteService.setSongService(songService);
        favouriteService.setSharedPreferences(preferences);
    }

    @After
    public void cleanup() {
        preferences.edit().remove(CommonConstants.FAVOURITES_KEY).apply();
    }

    @Test
    public void save() {
        // given:
        SongDragDrop songDragDrop = new SongDragDrop(0, "foo", false);
        songDragDrop.setTamilTitle("bar");

        // when:
        favouriteService.save("service1", songDragDrop);

        // then:
        assertEquals("service1", favouriteService.find("service1").getName());
    }

    @Test
    public void addSongToExistingFavourite() {
        // given:
        SongDragDrop songDragDrop = new SongDragDrop(0, "foo", false);
        songDragDrop.setTamilTitle("bar");
        favouriteService.save("service1", songDragDrop);

        SongDragDrop songDragDrop2 = new SongDragDrop(2, "foo1", false);

        // when:
        favouriteService.save("service1", songDragDrop2);

        // then:
        Favourite result = favouriteService.find("service1");
        assertEquals(2, result.getDragDrops().size());
    }

    @Test
    public void saveSongs() {
        // given:
        SongDragDrop songDragDrop1 = new SongDragDrop(0, "foo", false);
        songDragDrop1.setTamilTitle("bar");

        SongDragDrop songDragDrop2 = new SongDragDrop(1, "foo1", false);
        List<SongDragDrop> songDragDrops = List.of(songDragDrop1, songDragDrop2);

        // when:
        favouriteService.save("service1", songDragDrops);

        // then:
        Favourite result = favouriteService.find("service1");
        assertEquals(2, result.getDragDrops().size());

    }

    @Test
    public void find() {
        // given:
        SongDragDrop songDragDrop = new SongDragDrop(0, "foo1", false);
        songDragDrop.setTamilTitle("bar1");

        // when:
        favouriteService.save("service1", songDragDrop);

        // then:
        assertEquals("service1", favouriteService.find("service1").getName());
    }

    @Test
    public void findNames() {
        // given:
        SongDragDrop songDragDrop1 = new SongDragDrop(0, "foo", false);
        songDragDrop1.setTamilTitle("bar");

        SongDragDrop songDragDrop2 = new SongDragDrop(1, "foo1", false);
        List<SongDragDrop> songDragDrops = List.of(songDragDrop1, songDragDrop2);
        favouriteService.save("service1", songDragDrops);

        // when:
        List<String> result = favouriteService.findNames();

        // then:
        assertEquals(1, result.size());
        assertEquals("service1", result.get(0));
    }

    @Test
    public void buildShareFavouriteFormat() {
        // given: "favourite \"service1\" exists with two songs"
        SongDragDrop songDragDrop1 = new SongDragDrop(1, "foo", false);
        songDragDrop1.setTamilTitle("bar");

        SongDragDrop songDragDrop2 = new SongDragDrop(2, "foo1", false);
        List<SongDragDrop> songDragDrops = List.of(songDragDrop1, songDragDrop2);
        favouriteService.save("service1", songDragDrops);

        // when: "build share favourite format "
        String result = favouriteService.buildShareFavouriteFormat("service1");

        // then:
        assertTrue(result.contains("foo"));
        assertTrue(result.contains("bar"));
        assertTrue(result.contains("https://mcruncher.github.io/worshipsongs/?c2VydmljZTE7MTsyOw=="));
    }

    @Test
    public void remove() {
        // given:
        SongDragDrop songDragDrop = new SongDragDrop(1, "foo", false);
        songDragDrop.setTamilTitle("bar");
        favouriteService.save("service1", songDragDrop);

        // when:
        favouriteService.remove("service1");

        // then:
        assertNull(favouriteService.find("service1").getName());
    }

    @Test
    public void removeSong() {
        // given:
        SongDragDrop songDragDrop1 = new SongDragDrop(0, "foo", false);
        songDragDrop1.setTamilTitle("bar");

        SongDragDrop songDragDrop2 = new SongDragDrop(1, "foo1", false);
        List<SongDragDrop> songDragDrops = List.of(songDragDrop1, songDragDrop2);
        favouriteService.save("service1", songDragDrops);

        // when:
        favouriteService.removeSong("service1", "foo");

        // then:
        Favourite result = favouriteService.find("service1");
        assertEquals(1, result.getDragDrops().size());
        assertEquals("foo1", result.getDragDrops().get(0).getTitle());

    }

    @Test
    public void getNewOrderNumber() {
        // setup:
        List<Favourite> favorites = new ArrayList<>();
        favorites.add(new Favourite(1, "first favourite", new ArrayList<>()));
        favorites.add(new Favourite(3, "latest favourite", new ArrayList<>()));
        favorites.add(new Favourite(2, "second favourite", new ArrayList<>()));

        // expect:
        assertEquals(4, favouriteService.getFavouritesNewOrderNumber(favorites));
    }
}
