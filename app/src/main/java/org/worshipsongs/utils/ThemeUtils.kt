package org.worshipsongs.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity


import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.domain.Theme

/**
 * Author: Madasamy
 * Version: 3.3.x
 */

object ThemeUtils
{

    fun setTheme(appCompatActivity: AppCompatActivity)
    {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity)
        val theme = sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name)
        if (Theme.DAY.name.equals(theme!!, ignoreCase = true))
        {
            appCompatActivity.setTheme(R.style.DayTheme)
        } else
        {
            appCompatActivity.setTheme(R.style.NightTheme)
        }
    }

    fun setNoActionBarTheme(appCompatActivity: AppCompatActivity)
    {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity)
        val theme = sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name)
        if (Theme.DAY.name.equals(theme!!, ignoreCase = true))
        {
            appCompatActivity.setTheme(R.style.DayTheme_NoActionBar)
        } else
        {
            appCompatActivity.setTheme(R.style.NightTheme_NoActionBar)
        }
    }
}//Do nothing
