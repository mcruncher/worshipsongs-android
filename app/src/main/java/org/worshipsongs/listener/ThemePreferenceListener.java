package org.worshipsongs.listener;

import android.content.Intent;
import android.preference.Preference;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.domain.Theme;
import org.worshipsongs.fragment.SettingsPreferenceFragment;

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
public class ThemePreferenceListener implements Preference.OnPreferenceChangeListener
{
    private static final String DEFAULT_PRIMARY_COLOR_KEY = "defaultPrimaryColor";
    private static final String DEFAULT_SECONDARY_COLOR_KEY = "defaultSecondaryColor";
    private SettingsPreferenceFragment fragment;
    private UserSettingActivity userSettingActivity;

    public ThemePreferenceListener(UserSettingActivity userSettingActivity, SettingsPreferenceFragment fragment)
    {
        this.userSettingActivity = userSettingActivity;
        this.fragment = fragment;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String themeName = preference.getSharedPreferences().getString(
                CommonConstants.THEME_KEY, Theme.DAY.name());
        String newTheme = String.valueOf(newValue);
        if (!themeName.equalsIgnoreCase(newTheme)) {
            setColor(preference, newTheme);
            updateThemeAndRecreateActivity(preference, newTheme);
        }
        return false;
    }

    void setColor(Preference preference, String theme)
    {
        if (Theme.NIGHT.name().equalsIgnoreCase(theme)) {
            setDefaultColor(preference, DEFAULT_PRIMARY_COLOR_KEY, CommonConstants.PRIMARY_COLOR_KEY,
                    DEFAULT_SECONDARY_COLOR_KEY, CommonConstants.SECONDARY_COLOR_KEY);
            preference.getSharedPreferences().edit().putInt(CommonConstants.PRIMARY_COLOR_KEY, 0xffffffff).apply();
            preference.getSharedPreferences().edit().putInt(CommonConstants.SECONDARY_COLOR_KEY, 0xffffff00).apply();
        } else {
            setDefaultColor(preference, CommonConstants.PRIMARY_COLOR_KEY, DEFAULT_PRIMARY_COLOR_KEY,
                    CommonConstants.SECONDARY_COLOR_KEY, DEFAULT_SECONDARY_COLOR_KEY);
        }
    }

    private void setDefaultColor(Preference preference, String defaultPrimaryColorKey,
                                 String primaryColorKey, String defaultSecondaryColorKey,
                                 String secondaryColorKey)
    {
        preference.getSharedPreferences().edit().putInt(defaultPrimaryColorKey,
                preference.getSharedPreferences().getInt(primaryColorKey,
                        -12303292)).apply();
        preference.getSharedPreferences().edit().putInt(defaultSecondaryColorKey,
                preference.getSharedPreferences().getInt(secondaryColorKey,
                        -65536)).apply();
    }

    void updateThemeAndRecreateActivity(Preference preference, String newTheme)
    {
        preference.getSharedPreferences().edit().putString(CommonConstants.THEME_KEY,
                newTheme).apply();
        preference.getSharedPreferences().edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY,
                true).apply();
        userSettingActivity.invalidateOptionsMenu();
        userSettingActivity.finish();
        fragment.startActivity(new Intent(WorshipSongApplication.getContext(), UserSettingActivity.class));
    }
}
