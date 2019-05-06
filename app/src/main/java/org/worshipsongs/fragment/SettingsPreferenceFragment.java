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
    private static final String DEFAULT_PRIMARY_COLOR_KEY = "defaultPrimaryColor";
    private static final String DEFAULT_SECONDARY_COLOR_KEY = "defaultSecondaryColor";
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
                    setColor(preference, newTheme);
                    updateThemeAndRecreateActivity(preference, newTheme);
                }
                return false;
            }
        };
    }

    private void setColor(Preference preference, String theme)
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