package org.worshipsongs.service;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@RunWith(AndroidJUnit4.class)
public class UserPreferenceSettingServiceTest
{
    private UserPreferenceSettingService userPreferenceSettingService = new UserPreferenceSettingService();

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
        assertTrue(userPreferenceSettingService.isKeepAwake());
    }

    @Test
    public void testIsPlayVideo()
    {
        assertTrue(userPreferenceSettingService.isPlayVideo());
    }

}
