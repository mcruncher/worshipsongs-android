package org.worshipsongs.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.worshipsongs.fragment.SettingsPreferenceFragment;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class UserSettingActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferenceFragment()).commit();
    }

    public void activityFinish() {
        finish();
    }
}

