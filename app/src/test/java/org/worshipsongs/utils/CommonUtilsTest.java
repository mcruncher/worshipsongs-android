package org.worshipsongs.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.CommonConstants;

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CommonUtilsTest
{
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

    @After
    public void tearDown()
    {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void isProductionMode()
    {
        assertTrue(CommonUtils.INSTANCE.isProductionMode());
    }

    @Test
    public void isLolliPopOrGreater()
    {
        assertTrue(CommonUtils.INSTANCE.isLollipopOrGreater());
    }

    @Test
    public void isAboveOreoWhenRunningOnSdk28OrAbove()
    {
        assertTrue(CommonUtils.INSTANCE.isAboveOreo());
    }

    @Test
    @Config(sdk = 27)
    public void isAboveOreoWhenRunningOnSdk27()
    {
        assertFalse(CommonUtils.INSTANCE.isAboveOreo());
    }

    //Note: Update this test every major release
    @Test
    public void testGetProjectVersion()
    {
        String version = CommonUtils.INSTANCE.getProjectVersion();
        assertTrue(version.contains("3."));
    }

    @Test
    public void testIsNotImportedDatabase() throws Exception
    {
        assertTrue(CommonUtils.INSTANCE.isNotImportedDatabase());
    }

    @Test
    public void testIsImportedDatabase() throws Exception
    {
        sharedPreferences.edit().putBoolean(CommonConstants.INSTANCE.getSHOW_REVERT_DATABASE_BUTTON_KEY(), true).apply();
        assertFalse(CommonUtils.INSTANCE.isNotImportedDatabase());
    }


    @Test
    public void testIsNewVersion()
    {
       assertTrue(CommonUtils.INSTANCE.isNewVersion("3.x", "100.34"));
    }

    @Test
    public void testIsNewVersionEmptyVersionInPropertyFile()
    {
        assertTrue(CommonUtils.INSTANCE.isNewVersion("3.x", ""));
    }

    @Test
    public void testIsNotNewVersion() throws PackageManager.NameNotFoundException
    {

        assertFalse(CommonUtils.INSTANCE.isNewVersion("3.x", "3.x"));
    }
}