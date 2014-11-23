package org.worshipsongs.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.worship.R;
import org.worshipsongs.picker.ColorPickerPreference;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class UserSettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        Context context = WorshipSongApplication.getContext();
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            ((ColorPickerPreference) findPreference("color2")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    Log.d(UserSettingActivity.class.getName(), "Font Color:" + ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    return true;
                }

            });
            ((ColorPickerPreference) findPreference("color2")).setAlphaSliderEnabled(true);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference pref = findPreference(key);
        if (pref instanceof ColorPickerPreference) {
            ColorPickerPreference etp = (ColorPickerPreference) pref;
            if (pref.getKey().equals("color2")) {
                Log.d(this.getClass().getName(), "Get Color value onChanged" + etp.getValue());
                pref.setSummary(etp.getValue());
            }
        }
        if (pref instanceof ListPreference) {
            ListPreference etp = (ListPreference) pref;
            if (pref.getKey().equals("prefSetFont")) {
                pref.setSummary(etp.getValue());
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return true;
    }
}