package org.worshipsongs.activity

import android.annotation.TargetApi
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import org.worshipsongs.R

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [22])
class DatabaseSettingActivityTest {
    lateinit var databaseSettingActivity: ActivityScenario<DatabaseSettingActivity>

    @Before
    fun setUp() {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        sharedPreferences.edit().putBoolean("production", false).apply()
        databaseSettingActivity = ActivityScenario.launch(
            DatabaseSettingActivity::class.java
        )
    }

    @Test
    fun `Import database button properties`() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            val importDataBaseButton = activity.findViewById<Button>(R.id.upload_database_button)
            assertEquals("Import OpenLP database", importDataBaseButton.text)
            assertEquals(-1, importDataBaseButton.textColors.defaultColor.toLong())
        }
    }

    @Test
    fun `On click import database button`() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            val importDataBaseButton = activity.findViewById<Button>(R.id.upload_database_button)
            assertTrue(importDataBaseButton.performClick())
            val dialog = ShadowDialog.getLatestDialog() as AlertDialog
            assertEquals(2, dialog.listView.adapter.count.toLong())
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    fun `Revert to default database button`() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            val defaultDatabaseButton =
                activity.findViewById<View>(R.id.default_database_button) as Button
            assertEquals(8, defaultDatabaseButton.visibility.toLong())
        }
    }

    @Test
    fun `On click revert to default database button`() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            val defaultDatabaseButton =
                activity.findViewById<View>(R.id.default_database_button) as Button
            assertTrue(defaultDatabaseButton.performClick())
        }
    }

    @Test
    fun testProperties() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            assertEquals(
                "Are you sure that you want to revert to the default database?",
                activity.getString(R.string.message_database_confirmation)
            )
            assertEquals("Warning", activity.getString(R.string.warning))
            assertEquals(
                "You have chosen an invalid database! Please choose a valid database.",
                activity.getString(R.string.message_database_invalid)
            )
            assertEquals("Import OpenLP database from", activity.getString(R.string.type))
            assertEquals(
                "Database copied successfully",
                activity.getString(R.string.message_database_successfull)
            )
            assertEquals(
                "Make sure your device is connected to the internet. You can configure either Settings ->" +
                        " Wi-Fi or Settings -> Mobile Data.",
                activity.getString(R.string.message_network_warning)
            )
        }
    }

    @Test
    fun `Get destination file`() {
        databaseSettingActivity.onActivity { activity: DatabaseSettingActivity ->
            assertEquals(
                "songs.sqlite",
                activity.destinationFile.name
            )
        }
    }
}