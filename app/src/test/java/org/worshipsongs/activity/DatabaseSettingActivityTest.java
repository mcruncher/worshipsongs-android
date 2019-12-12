package org.worshipsongs.activity;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.worshipsongs.R;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class DatabaseSettingActivityTest
{
    private ActivityScenario<DatabaseSettingActivity> databaseSettingActivity;

    @Before
    public void setUp()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
        sharedPreferences.edit().putBoolean("production", false).apply();
        databaseSettingActivity = ActivityScenario.launch(DatabaseSettingActivity.class);
    }

    @Test
    public void testImportDatabaseButton_properties()
    {
        System.out.println("--importDatabaseButton--");
        databaseSettingActivity.onActivity(activity -> {
            Button importDataBaseButton = activity.findViewById(R.id.upload_database_button);
            assertEquals("Import OpenLP database", importDataBaseButton.getText());
            assertEquals(-1, importDataBaseButton.getTextColors().getDefaultColor());
        });
    }

    @Test
    public void testOnClickImportDatabaseButton()
    {
        System.out.println("--onClickImportDatabaseButton--");
        databaseSettingActivity.onActivity(activity -> {
            Button importDataBaseButton = activity.findViewById(R.id.upload_database_button);
            assertTrue(importDataBaseButton.performClick());
            AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
            assertEquals(2, dialog.getListView().getAdapter().getCount());
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testRevertToDefaultDatabaseButton()
    {
        System.out.println("--revertToDefaultDatabaseButton--");
        databaseSettingActivity.onActivity(activity -> {
            Button defaultDatabaseButton = (Button) activity.findViewById(R.id.default_database_button);
            assertEquals(8, defaultDatabaseButton.getVisibility());
        });
    }

    @Test
    public void testOnClickRevertToDefaultDatabaseButton()
    {
        System.out.println("--revertToDefaultDatabaseButton--");
        databaseSettingActivity.onActivity(activity -> {
            Button defaultDatabaseButton = (Button) activity.findViewById(R.id.default_database_button);
            assertTrue(defaultDatabaseButton.performClick());
        });
    }


    @Test
    public void testProperties()
    {
        databaseSettingActivity.onActivity(activity -> {
            assertEquals("Are you sure that you want to revert to the default database?",
                    activity.getString(R.string.message_database_confirmation));
            assertEquals("Warning", activity.getString(R.string.warning));
            assertEquals("You have chosen an invalid database! Please choose a valid database.",
                    activity.getString(R.string.message_database_invalid));
            assertEquals("Import OpenLP database from", activity.getString(R.string.type));
            assertEquals("Database copied successfully", activity.getString(R.string.message_database_successfull));
            assertEquals("Make sure your device is connected to the internet. You can configure either Settings ->" +
                    " Wi-Fi or Settings -> Mobile Data.", activity.getString(R.string.message_network_warning));
        });
    }

    @Test
    public void testGetDestinationFile()
    {
        System.out.println("--destinationFile--");
        databaseSettingActivity.onActivity(activity -> {
            assertEquals("songs.sqlite", activity.getDestinationFile().getName());
        });
    }

}
