package org.worshipsongs.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import org.worshipsongs.page.component.fragment.WorshipSongsPreference;
import org.worshipsongs.worship.R;

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

