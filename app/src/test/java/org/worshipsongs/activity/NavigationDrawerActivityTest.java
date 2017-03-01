package org.worshipsongs.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.worship.BuildConfig;
import org.worshipsongs.worship.R;

import static org.junit.Assert.assertEquals;

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
public class NavigationDrawerActivityTest
{
    private NavigationDrawerActivity navigationDrawerActivity;

    @Before
    public void setUp()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        sharedPreferences.edit().putBoolean("production", false).apply();
        navigationDrawerActivity = new NavigationDrawerActivity();
    }

    @Test
    public void testGetFlags()
    {
        assertEquals(1208483840, navigationDrawerActivity.getFlags());
    }

    @Test
    public void testProperties()
    {
        assertEquals("Settings", RuntimeEnvironment.application.getApplicationContext().getString(R.string.settings));
        assertEquals("Song Book", RuntimeEnvironment.application.getApplicationContext().getString(R.string.home));
        assertEquals("Rate this app", RuntimeEnvironment.application.getApplicationContext().getString(R.string.rate_this_app));
        assertEquals("Feedback", RuntimeEnvironment.application.getApplicationContext().getString(R.string.feedback));
    }

}