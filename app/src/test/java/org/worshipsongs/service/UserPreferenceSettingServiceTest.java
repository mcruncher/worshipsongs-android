package org.worshipsongs.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * @author Madasamy
 * @since 3.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class UserPreferenceSettingServiceTest
{
    private UserPreferenceSettingService userPreferenceSettingService = new UserPreferenceSettingService();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

    @After
    public void tearDown()
    {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testGetPortraitFontSize()
    {
        assertEquals(18, userPreferenceSettingService.getPortraitFontSize(), 0);
    }

    @Test
    public void testGetLandScapeFontSize()
    {
        assertEquals(25, userPreferenceSettingService.getLandScapeFontSize(), 0);
    }

    @Test
    public void testGetPrimaryColor()
    {
        assertEquals(-12303292, userPreferenceSettingService.getPrimaryColor());
    }

    @Test
    public void testSecondaryColor()
    {
        assertEquals(-65536, userPreferenceSettingService.getSecondaryColor());
    }

    @Test
    public void testGetPresentationBackgroundColor()
    {
        assertEquals(-16777216, userPreferenceSettingService.getPresentationBackgroundColor());
    }

    @Test
    public void testGetPresentationPrimaryColor()
    {
        assertEquals(-1, userPreferenceSettingService.getPresentationPrimaryColor());
    }

    @Test
    public void testPresentationSecondaryColor()
    {
        assertEquals(-256, userPreferenceSettingService.getPresentationSecondaryColor());
    }

    @Test
    public void testIsKeepAwake()
    {
        sharedPreferences.edit().putBoolean("prefKeepAwakeOn", true).apply();
        assertTrue(userPreferenceSettingService.isKeepAwake());
    }

    @Test
    public void testIsPlayVideo()
    {
        assertTrue(userPreferenceSettingService.isPlayVideo());
    }

    @Test
    public void displaySongBookWhenRunningOnSdk28OrAbove()
    {
        // by default, it should be false
        assertFalse(userPreferenceSettingService.getDisplaySongBook());

        // given the user has turned it on
        sharedPreferences.edit().putBoolean("prefDisplaySongbook", true).apply();

        // it should be true
        assertTrue(userPreferenceSettingService.getDisplaySongBook());
    }

    @Test
    @Config(sdk = 27)
    public void displaySongBookWhenRunningOnSdk27()
    {
        // it should be false
        sharedPreferences.edit().putBoolean("prefDisplaySongbook", true).apply();
        assertFalse(userPreferenceSettingService.getDisplaySongBook());
    }
}
