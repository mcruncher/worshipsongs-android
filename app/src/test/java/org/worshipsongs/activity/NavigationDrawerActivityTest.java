package org.worshipsongs.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
    public void testEmailSubject() throws PackageManager.NameNotFoundException
    {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        String expected = "Worship Songs Android Feedback - "+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        assertEquals(expected, navigationDrawerActivity.getEmailSubject(context));
    }

    @Test
    public void testProperties()
    {
        assertEquals("Settings", RuntimeEnvironment.application.getApplicationContext().getString(R.string.settings));
        assertEquals("Song Book", RuntimeEnvironment.application.getApplicationContext().getString(R.string.home));
        assertEquals("Rate us", RuntimeEnvironment.application.getApplicationContext().getString(R.string.rate_this_app));
        assertEquals("Send feedback", RuntimeEnvironment.application.getApplicationContext().getString(R.string.feedback));
        assertEquals("Version:", RuntimeEnvironment.application.getApplicationContext().getString(R.string.version));
        assertEquals("Tamil Christian Worship Songs Android app brings you most of the Praise & Worship lyrics used by " +
                "Tamil churches all over the world. Now, you can worship anytime, anywhere with" +
                " all the lyrics you need.\n", RuntimeEnvironment.application.getApplicationContext().getString(R.string.app_description));
        assertEquals("You can download this app here: https://play.google.com/store/apps/details?id=org.worshipsongs",
                RuntimeEnvironment.application.getApplicationContext().getString(R.string.app_download_info));
    }

}