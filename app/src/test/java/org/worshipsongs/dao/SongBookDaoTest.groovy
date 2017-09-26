package org.worshipsongs.dao

import android.preference.PreferenceManager
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.worshipsongs.CommonConstants

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class SongBookDaoTest extends ElectricSpecification
{
    def songBookDao = new SongBookDao()
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
        def result = songBookDao.parseName("Foo={இடைவிடா நன்றி உமக்குத்தான}")

        then:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Parse default name"()
    {
        given:
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply()

        when:
        def result = songBookDao.parseName("Foo{இடைவிடா நன்றி உமக்குத்தான}")

        then:
        result == "Foo"
    }

    def "Get tamil name"()
    {
        setup:
        def result = songBookDao.getTamilName("Foo={இடைவிடா நன்றி உமக்குத்தான}")

        expect:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Get tamil name from null"()
    {
        setup:
        def result = songBookDao.getTamilName(null)

        expect:
        result == ""
    }

    def "Get tamil name from empty string"()
    {
        setup:
        def result = songBookDao.getTamilName("")

        expect:
        result == ""

    }

    def "Get default name"()
    {
        setup:
        def result = songBookDao.getDefaultName("foo {ahhjajhf}")

        expect:
        result == "foo"
    }

    def "Get default name from null"()
    {
        setup:
        def result = songBookDao.getDefaultName(null)

        expect:
        result == ""
    }

    def "Get default name from empty string"()
    {
        setup:
        def result = songBookDao.getDefaultName("")

        expect:
        result == ""
    }
}
