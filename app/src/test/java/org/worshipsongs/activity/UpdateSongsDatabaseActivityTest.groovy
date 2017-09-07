package org.worshipsongs.activity

import android.os.Bundle
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class UpdateSongsDatabaseActivityTest extends ElectricSpecification
{
    def updateSongsDatabaseActivity;

    void setup()
    {
        updateSongsDatabaseActivity = Robolectric.setupActivity(UpdateSongsDatabaseActivity.class)
    }

    def "Should not null"()
    {
        expect:
        updateSongsDatabaseActivity != null
    }

    def "On back pressed"()
    {
        setup:
        updateSongsDatabaseActivity.onBackPressed()

        expect:
        updateSongsDatabaseActivity.isFinishing()
    }

    def "On positive button pressed"()
    {
        setup:
        updateSongsDatabaseActivity.onClickPositiveButton(new Bundle(), "")

        expect:
        updateSongsDatabaseActivity.isFinishing()
    }
}
