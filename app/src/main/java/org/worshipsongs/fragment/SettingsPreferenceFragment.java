package org.worshipsongs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.domain.Theme;
import org.worshipsongs.preference.LanguagePreference;
import org.worshipsongs.preference.PreferenceListener;
import org.worshipsongs.preference.ThemeListPreference;

/**
 * Author:Seenivasan, Madasamy
 * version:1.0.0
 */

public class SettingsPreferenceFragment extends PreferenceFragment implements PreferenceListener
{
    private UserSettingActivity userSettingActivity = new UserSettingActivity();


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        languagePreference();
        themePreference();
        resetPreferenceSettings("resetDialog");
    }

    private void languagePreference()
    {
        LanguagePreference resetDialogPreference = (LanguagePreference) findPreference(
                "languagePreference");
        resetDialogPreference.setPreferenceListener(this);
    }

    private void themePreference()
    {
        ThemeListPreference themeListPreference = (ThemeListPreference) findPreference(
                CommonConstants.THEME_KEY);
        themeListPreference.setOnPreferenceChangeListener(getOnPreferenceChangeListener());
    }

    @NonNull
    private Preference.OnPreferenceChangeListener getOnPreferenceChangeListener()
    {
        return new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                String themeName = preference.getSharedPreferences().getString(
                        CommonConstants.THEME_KEY, Theme.DAY.name());
                String newTheme = String.valueOf(newValue);
                if (!themeName.equalsIgnoreCase(newTheme)) {
                    setColor(preference, "primaryColor", -12303292,
                            0xffffffff);
                    setColor(preference, "secondaryColor", -65536,
                            0xffffff00);
                    updateThemeAndRecreateActivity(preference, newTheme);
                }
                return false;
            }
        };
    }

    private void setColor(Preference preference, String colorKey, int defaultColor,
                          int updatedColor)
    {
        int primaryColor = preference.getSharedPreferences().getInt(colorKey, defaultColor);
        if (primaryColor == defaultColor) {
            preference.getSharedPreferences().edit().putInt(colorKey, updatedColor).apply();
        } else if (primaryColor == updatedColor) {
            preference.getSharedPreferences().edit().putInt(colorKey, defaultColor).apply();
        }
    }

    private void updateThemeAndRecreateActivity(Preference preference, String newTheme)
    {
        preference.getSharedPreferences().edit().putString(CommonConstants.THEME_KEY,
                newTheme).apply();
        preference.getSharedPreferences().edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY,
                true).apply();
        userSettingActivity.invalidateOptionsMenu();
        userSettingActivity.finish();
        startActivity(new Intent(WorshipSongApplication.getContext(), UserSettingActivity.class));
    }

    public void resetPreferenceSettings(String preferenceKey)
    {
        Preference resetDialogPreference = findPreference(preferenceKey);
        resetDialogPreference.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue)
                    {
                        Intent startIntent = new Intent(WorshipSongApplication.getContext(),
                                UserSettingActivity.class);
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        userSettingActivity.activityFinish();
                        startActivity(startIntent);
                        return false;
                    }
                });
    }

    public void setUserSettingActivity(UserSettingActivity userSettingActivity)
    {
        this.userSettingActivity = userSettingActivity;
    }

    @Override
    public void onSelect()
    {
        userSettingActivity.invalidateOptionsMenu();
        userSettingActivity.finish();
        Intent startIntent = new Intent(WorshipSongApplication.getContext(),
                UserSettingActivity.class);
        startActivity(startIntent);

    }
}