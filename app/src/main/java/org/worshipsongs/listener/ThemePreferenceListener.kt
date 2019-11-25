package org.worshipsongs.listener

import android.content.Intent
import android.preference.Preference

import org.worshipsongs.CommonConstants
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.activity.UserSettingActivity
import org.worshipsongs.domain.Theme
import org.worshipsongs.fragment.SettingsPreferenceFragment

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ThemePreferenceListener(private val userSettingActivity: UserSettingActivity, private val fragment: SettingsPreferenceFragment) : Preference.OnPreferenceChangeListener
{

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean
    {
        val themeName = preference.sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name)
        val newTheme = newValue.toString()
        if (!themeName!!.equals(newTheme, ignoreCase = true))
        {
            setColor(preference, newTheme)
            updateThemeAndRecreateActivity(preference, newTheme)
        }
        return false
    }

    internal fun setColor(preference: Preference, theme: String)
    {
        if (Theme.NIGHT.name.equals(theme, ignoreCase = true))
        {
            setDefaultColor(preference, DEFAULT_PRIMARY_COLOR_KEY, CommonConstants.PRIMARY_COLOR_KEY, DEFAULT_SECONDARY_COLOR_KEY, CommonConstants.SECONDARY_COLOR_KEY)
            preference.sharedPreferences.edit().putInt(CommonConstants.PRIMARY_COLOR_KEY, -0x1).apply()
            preference.sharedPreferences.edit().putInt(CommonConstants.SECONDARY_COLOR_KEY, -0x100).apply()
        } else
        {
            setDefaultColor(preference, CommonConstants.PRIMARY_COLOR_KEY, DEFAULT_PRIMARY_COLOR_KEY, CommonConstants.SECONDARY_COLOR_KEY, DEFAULT_SECONDARY_COLOR_KEY)
        }
    }

    private fun setDefaultColor(preference: Preference, defaultPrimaryColorKey: String, primaryColorKey: String, defaultSecondaryColorKey: String, secondaryColorKey: String)
    {
        preference.sharedPreferences.edit().putInt(defaultPrimaryColorKey, preference.sharedPreferences.getInt(primaryColorKey, -12303292)).apply()
        preference.sharedPreferences.edit().putInt(defaultSecondaryColorKey, preference.sharedPreferences.getInt(secondaryColorKey, -65536)).apply()
    }

    internal fun updateThemeAndRecreateActivity(preference: Preference, newTheme: String)
    {
        preference.sharedPreferences.edit().putString(CommonConstants.THEME_KEY, newTheme).apply()
        preference.sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply()
        userSettingActivity.invalidateOptionsMenu()
        userSettingActivity.finish()
        fragment.startActivity(Intent(WorshipSongApplication.getContext(), UserSettingActivity::class.java))
    }

    companion object
    {
        private val DEFAULT_PRIMARY_COLOR_KEY = "defaultPrimaryColor"
        private val DEFAULT_SECONDARY_COLOR_KEY = "defaultSecondaryColor"
    }
}
