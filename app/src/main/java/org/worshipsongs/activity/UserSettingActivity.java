package org.worshipsongs.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.worshipsongs.fragment.WorshipSongsPreference;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class UserSettingActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new WorshipSongsPreference()).commit();
    }

    public void activityFinish() {
        finish();
    }
}

