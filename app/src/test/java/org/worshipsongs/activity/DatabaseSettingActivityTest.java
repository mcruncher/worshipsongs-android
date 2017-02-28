package org.worshipsongs.activity;

import android.annotation.TargetApi;


import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDialog;
import org.worshipsongs.worship.BuildConfig;
import org.worshipsongs.worship.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class DatabaseSettingActivityTest
{
    private DatabaseSettingActivity databaseSettingActivity;

    @Before
    public void setUp()
    {
        databaseSettingActivity = Robolectric.setupActivity(DatabaseSettingActivity.class);
    }

    @Test
    public void testImportDatabaseButton_properties()
    {
        System.out.println("--importDatabaseButton--");
        Button importDataBaseButton = (Button) databaseSettingActivity.findViewById(R.id.upload_database_button);
        assertEquals("Import database", importDataBaseButton.getText());
        assertEquals(-1, importDataBaseButton.getTextColors().getDefaultColor());
    }

    @Test
    public void testOnClickImportDatabaseButton()
    {
        System.out.println("--onClickImportDatabaseButton--");
        Button importDataBaseButton = (Button) databaseSettingActivity.findViewById(R.id.upload_database_button);
        assertTrue(importDataBaseButton.performClick());
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertEquals(2, dialog.getListView().getAdapter().getCount());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testRevertToDefaultDatabaseButton()
    {
        System.out.println("--revertToDefaultDatabaseButton--");
        Button defaultDatabaseButton = (Button) databaseSettingActivity.findViewById(R.id.default_database_button);
        assertEquals(8, defaultDatabaseButton.getVisibility());
    }

    @Test
    public void testOnClickRevertToDefaultDatabaseButton()
    {
        System.out.println("--revertToDefaultDatabaseButton--");
        Button defaultDatabaseButton = (Button) databaseSettingActivity.findViewById(R.id.default_database_button);
        assertTrue(defaultDatabaseButton.performClick());
    }

    @Test
    public void testProperties()
    {
        assertEquals("Are you sure that you want to revert to the default database?",
                databaseSettingActivity.getString(R.string.message_database_confirmation));
        assertEquals("Warning", databaseSettingActivity.getString(R.string.warning));
        assertEquals("You have chosen an invalid database! Please choose a valid database.",
                databaseSettingActivity.getString(R.string.message_database_invalid));
        assertEquals("Import database from", databaseSettingActivity.getString(R.string.type));
        assertEquals("Database copied successfully", databaseSettingActivity.getString(R.string.message_database_successfull));
        assertEquals("Make sure your device is connected to the internet. You can configure either Settings ->" +
                " Wi-Fi or Settings -> Mobile Data.", databaseSettingActivity.getString(R.string.message_network_warning));
    }

    @Test
    public void testGetDestinationFile()
    {
        System.out.println("--destinationFile--");
        assertEquals("songs.sqlite", databaseSettingActivity.getDestinationFile().getName());
    }

}