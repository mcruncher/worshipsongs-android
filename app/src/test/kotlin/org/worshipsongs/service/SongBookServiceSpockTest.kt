package org.worshipsongs.service

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.SongBook

/**
 *  Author : Madasamy
 *  Version : 3.x
 */

@RunWith(RobolectricTestRunner::class)
class SongBookServiceSpockTest {
    val songBookService = SongBookService(ApplicationProvider.getApplicationContext())
    val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    @After
    fun cleanup() {
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply()
    }

    @Test
    fun `Parse tamil Name`() {
        // given:
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply()

        // when:
        val result = songBookService.parseName("Foo={இடைவிடா நன்றி உமக்குத்தான}")

        // then:
        assertEquals("இடைவிடா நன்றி உமக்குத்தான", result)
    }

    @Test
    fun `Parse default name`() {
        // given:
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply()

        // when:
        val result = songBookService.parseName("Foo{இடைவிடா நன்றி உமக்குத்தான}")

        // then:
        assertEquals("Foo", result)
    }

    @Test
    fun `Filtered song books`() {
        // given:
        val songs = ArrayList<SongBook>()
        songs.add(SongBook("foo"))

        // when:
        val result = songBookService.filteredSongBooks("fo", songs)

        // then:
        assertEquals(1, result.size)
    }

    @Test
    fun `Filtered song books unknown query`() {
        // given:
        val songs = ArrayList<SongBook>()
        songs.add(SongBook("foo"))

        // when:
        val result = songBookService.filteredSongBooks("ba", songs)

        // then:
        assertEquals(0, result.size)
    }

}
