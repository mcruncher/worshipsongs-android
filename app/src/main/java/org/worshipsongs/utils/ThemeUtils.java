package org.worshipsongs.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.domain.Theme;

/**
 * Author: Madasamy
 * Version: 3.3.x
 */

public final class ThemeUtils
{


    private ThemeUtils()
    {
        //Do nothing
    }

    public static void setTheme(AppCompatActivity appCompatActivity)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        String theme = sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name());
        if (Theme.DAY.name().equalsIgnoreCase(theme)) {
            appCompatActivity.setTheme(R.style.DayTheme);
        } else {
            appCompatActivity.setTheme(R.style.NightTheme);
        }
    }

    public static void setNoActionBarTheme(AppCompatActivity appCompatActivity)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        String theme = sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name());
        if (Theme.DAY.name().equalsIgnoreCase(theme)) {
            appCompatActivity.setTheme(R.style.DayTheme_NoActionBar);
        } else {
            appCompatActivity.setTheme(R.style.NightTheme_NoActionBar);
        }
    }
}
