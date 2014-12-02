package org.worshipsongs.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.worship.R;
import org.worshipsongs.picker.ColorPickerPreference;

import java.util.Map;

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

            ColorPickerPreference colorPickerPreference = (ColorPickerPreference)findPreference("color2");
            int color = colorPickerPreference.getValue();
            colorPickerPreference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color))));


            ((ColorPickerPreference) findPreference("color2")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    return true;
                }

            });
            ((ColorPickerPreference) findPreference("color2")).setAlphaSliderEnabled(true);

            ListPreference preferenceFont = (ListPreference) findPreference("prefSetFont");
            if(preferenceFont.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                preferenceFont.setValueIndex(0);
            }
            preferenceFont.setSummary(preferenceFont.getValue().toString());
            preferenceFont.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });

            ListPreference prefSetFontStyle = (ListPreference) findPreference("prefSetFontStyle");
            if(prefSetFontStyle.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                prefSetFontStyle.setValueIndex(0);
            }
            prefSetFontStyle.setSummary(prefSetFontStyle.getValue().toString());
            prefSetFontStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });

            ListPreference prefSetFontFace = (ListPreference) findPreference("prefSetFontFace");
            if(prefSetFontFace.getValue()==null) {
                prefSetFontFace.setValueIndex(0);
            }
            prefSetFontFace.setSummary(prefSetFontFace.getValue().toString());
            prefSetFontFace.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {

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