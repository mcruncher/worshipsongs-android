package org.worshipsongs.fragment


import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.worshipsongs.CommonConstants.THEME_KEY
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.activity.UserSettingActivity
import org.worshipsongs.listener.ThemePreferenceListener
import org.worshipsongs.preference.LanguagePreference
import org.worshipsongs.preference.PreferenceListener
import org.worshipsongs.preference.ThemeListPreference

/**
 * @author Madasamy
 * @since 1.0.0
 */

class SettingsPreferenceFragment : PreferenceFragmentCompat(), PreferenceListener
{

    private var userSettingActivity = UserSettingActivity()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.settings, rootKey)
        languagePreference()
       // themePreference()

    }

//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//       // addPreferencesFromResource(R.xml.settings)
////        languagePreference()
////        themePreference()
////        resetPreferenceSettings("resetDialog")
//    }

    private fun  languagePreference()
    {
        val preference = findPreference<LanguagePreference>("languagePreference")
        if(preference is LanguagePreference)
        {
            preference.setPreferenceListener(this)
        }

    }
//
    private fun themePreference()
    {
        val themeListPreference = findPreference<ThemeListPreference>(THEME_KEY)
        themeListPreference!!.onPreferenceChangeListener = ThemePreferenceListener(userSettingActivity, this)
    }

//    fun resetPreferenceSettings(preferenceKey: String)
//    {
//        val resetDialogPreference = findPreference(preferenceKey)
//        resetDialogPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
//            val startIntent = Intent(WorshipSongApplication.context, UserSettingActivity::class.java)
//            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            userSettingActivity.activityFinish()
//            startActivity(startIntent)
//            false
//        }
//    }

    fun setUserSettingActivity(userSettingActivity: UserSettingActivity)
    {
        this.userSettingActivity = userSettingActivity
    }

    override fun onSelect()
    {
        userSettingActivity.invalidateOptionsMenu()
        userSettingActivity.finish()
        val startIntent = Intent(WorshipSongApplication.context, UserSettingActivity::class.java)
        startActivity(startIntent)

    }
}