package org.worshipsongs.activity

import android.os.Bundle
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.worshipsongs.activity.UpdateSongsDatabaseActivity

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner::class)
class UpdateSongsDatabaseActivityTest {
    var updateSongsDatabaseActivity = Robolectric.setupActivity(
        UpdateSongsDatabaseActivity::class.java
    )

    @Test
    fun shouldNotNull() {
        // expect:
        assertNotNull(updateSongsDatabaseActivity)
    }

    @Test
    fun `On back pressed`() {
        // setup:
        updateSongsDatabaseActivity.onBackPressed()

        // expect:
        assertTrue(updateSongsDatabaseActivity.isFinishing)
    }

    @Test
    fun `On positive button pressed`() {
        // setup:
        updateSongsDatabaseActivity.onClickPositiveButton(Bundle(), "")

        // expect:
        assertTrue(updateSongsDatabaseActivity.isFinishing)
    }
}