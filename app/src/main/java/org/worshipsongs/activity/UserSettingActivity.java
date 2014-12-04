package org.worshipsongs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.picker.ColorPickerPreference;
import org.worshipsongs.worship.R;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class UserSettingActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public class MyPreferenceFragment extends PreferenceFragment {
        private Preference resetDialogPreference;
        private Intent startIntent;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            //Initialize Color Picker preference
            colorPickerSettingsListener("primaryColor");
            colorPickerSettingsListener("secondaryColor");

            //Initialize list preference
            listPreferenceSettingsListener("prefSetFont");
            listPreferenceSettingsListener("prefSetFontStyle");
            listPreferenceSettingsListener("prefSetFontFace");

            //Initialize Preference
            resetPreferenceListener("reset_settings");
        }

        private void resetPreferenceListener(String preferenceKey) {
            this.resetDialogPreference = getPreferenceScreen().findPreference(preferenceKey);
            this.startIntent = getIntent();

            //Set the OnPreferenceChangeListener for the resetDialogPreference
            this.resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //Both enter and exit animations are set to zero, so no transition animation is applied
                    overridePendingTransition(0, 0);
                    //Call this line, just to make sure that the system doesn't apply an animation
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //Close this Activity
                    finish();
                    //Again, don't set an animation for the transition
                    overridePendingTransition(0, 0);
                    startActivity(startIntent);
                    return false;
                }
            });
        }

        private void listPreferenceSettingsListener(String prefSetFont) {
            ListPreference preferenceFont = (ListPreference) findPreference(prefSetFont);
            setListPreferenceSettingValue(prefSetFont);
            preferenceFont.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });
        }

        private void colorPickerSettingsListener(String colorPickerKey) {
            setColorPickerPreferenceValue(colorPickerKey);
            ((ColorPickerPreference) findPreference(colorPickerKey)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    return true;
                }

            });
            ((ColorPickerPreference) findPreference(colorPickerKey)).setAlphaSliderEnabled(true);
        }

        private void setColorPickerPreferenceValue(String colorPickerKey) {
            ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
            int color = primaryColorPreference.getValue();
            primaryColorPreference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color))));
        }

        private void setListPreferenceSettingValue(String preferenceSettingKey) {
            ListPreference preferenceFont = (ListPreference) findPreference(preferenceSettingKey);
            if (preferenceFont.getValue() == null) {
                // to ensure we don't get a null value
                // set first value by default
                preferenceFont.setValueIndex(0);
            }
            preferenceFont.setSummary(preferenceFont.getValue().toString());

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}