package org.worshipsongs.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.activity.UserSettingActivity
import org.worshipsongs.listener.ThemePreferenceListener
import org.worshipsongs.preference.LanguagePreference
import org.worshipsongs.preference.PreferenceListener
import org.worshipsongs.preference.ThemeListPreference

import org.worshipsongs.CommonConstants.THEME_KEY

/**
 * Author:Seenivasan, Madasamy
 * version:1.0.0
 */

class SettingsPreferenceFragment : PreferenceFragment(), PreferenceListener
{
    private var userSettingActivity = UserSettingActivity()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        languagePreference()
        themePreference()
        resetPreferenceSettings("resetDialog")
    }

    private fun languagePreference()
    {
        val resetDialogPreference = findPreference("languagePreference") as LanguagePreference
        resetDialogPreference.setPreferenceListener(this)
    }

    private fun themePreference()
    {
        val themeListPreference = findPreference(THEME_KEY) as ThemeListPreference
        themeListPreference.onPreferenceChangeListener = ThemePreferenceListener(userSettingActivity, this)
    }

    fun resetPreferenceSettings(preferenceKey: String)
    {
        val resetDialogPreference = findPreference(preferenceKey)
        resetDialogPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val startIntent = Intent(WorshipSongApplication.getContext(), UserSettingActivity::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            userSettingActivity.activityFinish()
            startActivity(startIntent)
            false
        }
    }

    fun setUserSettingActivity(userSettingActivity: UserSettingActivity)
    {
        this.userSettingActivity = userSettingActivity
    }

    override fun onSelect()
    {
        userSettingActivity.invalidateOptionsMenu()
        userSettingActivity.finish()
        val startIntent = Intent(WorshipSongApplication.getContext(), UserSettingActivity::class.java)
        startActivity(startIntent)

    }
}