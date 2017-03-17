package org.worshipsongs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.worship.BuildConfig;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class CommonUtilsTest
{
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

    @After
    public void tearDown()
    {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testIsProductionMode()
    {
        System.out.println("--isProductionMode--");
        assertTrue(CommonUtils.isProductionMode());
    }


    @Test
    public void testIsJellyBeanMrOrGreater()
    {
        System.out.println("--isJellyBeanMrOrGreater--");
        assertTrue(CommonUtils.isJellyBeanMrOrGreater());
    }

    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void testIsNotJellyBeanMR()
    {
        System.out.println("--isNotJellyBeanMR--");
        assertFalse(CommonUtils.isJellyBeanMrOrGreater());
    }

    @Test
    public void testIsLolliPopOrGreater()
    {
        System.out.println("--isLolliPopOrGreater--");
        assertTrue(CommonUtils.isLollipopOrGreater());
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsNotLollipop()
    {
        System.out.println("--IsNotLollipop--");
        assertFalse(CommonUtils.isLollipopOrGreater());
    }

    //Note: Update this test every major release
    @Test
    public void testGetProjectVersion()
    {
        String version = CommonUtils.getProjectVersion();
        assertTrue(version.contains("3."));
    }

    @Test
    public void testIsNotImportedDatabase() throws Exception
    {
        assertTrue(CommonUtils.isNotImportedDatabase());
    }

    @Test
    public void testIsImportedDatabase() throws Exception
    {
        sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply();
        assertFalse(CommonUtils.isNotImportedDatabase());
    }


    @Test
    public void testIsNewVersion()
    {
       assertTrue(CommonUtils.isNewVersion("3.x", "100.34"));
    }

    @Test
    public void testIsNewVersionEmptyVersionInPropertyFile()
    {
        assertTrue(CommonUtils.isNewVersion("3.x", ""));
    }

    @Test
    public void testIsNotNewVersion() throws PackageManager.NameNotFoundException
    {

        assertFalse(CommonUtils.isNewVersion("3.x", "3.x"));
    }
}