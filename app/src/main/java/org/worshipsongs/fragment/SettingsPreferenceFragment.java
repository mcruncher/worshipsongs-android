package org.worshipsongs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;

/**
 * Author:Seenivasan, Madasamy
 * version:1.0.0
 */

public class SettingsPreferenceFragment extends PreferenceFragment
{
    private UserSettingActivity userSettingActivity = new UserSettingActivity();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        resetPreferenceSettings("resetDialog");
    }

    public void resetPreferenceSettings(String preferenceKey)
    {
        Preference resetDialogPreference = findPreference(preferenceKey);
        final Intent startIntent = new Intent(WorshipSongApplication.getContext(), UserSettingActivity.class);
        //Set the OnPreferenceChangeListener for the resetDialogPreference
        resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //Close this Activity
                userSettingActivity.activityFinish();
                startActivity(startIntent);
                return false;
            }
        });
    }

}