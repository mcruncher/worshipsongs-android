package org.worshipsongs.service

import android.preference.PreferenceManager
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.SongBook

/**
 *  Author : Madasamy
 *  Version : 3.x
 */
class SongBookServiceSpockTest extends ElectricSpecification
{
    def songBookService = new SongBookService(RuntimeEnvironment.application.getApplicationContext())
    def sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

    void cleanup()
    {
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply()
    }

    def "Parse tamil Name"()
    {
        given:
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply()

        when:
        def result = songBookService.parseName("Foo={இடைவிடா நன்றி உமக்குத்தான}")

        then:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Parse default name"()
    {
        given:
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply()

        when:
        def result = songBookService.parseName("Foo{இடைவிடா நன்றி உமக்குத்தான}")

        then:
        result == "Foo"
    }

    def "Filtered song books"()
    {
        given:
        def songs = new ArrayList<SongBook>()
        songs.add(new SongBook("foo"))

        when:
        def result = songBookService.filteredSongBooks("fo", songs)

        then:
        result.size() == 1
    }

    def "Filtered song books unknown query"()
    {
        given:
        def songs = new ArrayList<SongBook>()
        songs.add(new SongBook("foo"))

        when:
        def result = songBookService.filteredSongBooks("ba", songs)

        then:
        result.size() == 0
    }

}
