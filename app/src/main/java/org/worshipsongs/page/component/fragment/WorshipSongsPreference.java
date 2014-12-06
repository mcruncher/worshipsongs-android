package org.worshipsongs.page.component.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.picker.ColorPickerPreference;
import org.worshipsongs.worship.R;

/**
 * Created by Seenivasan on 12/6/2014.
 */
public class WorshipSongsPreference extends PreferenceFragment {

    private Preference resetDialogPreference;
    private Intent startIntent;
    private Context context = WorshipSongApplication.getContext();
    UserSettingActivity userSettingActivity = new UserSettingActivity();


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //Initialize Color Picker preference
        colorPickerSettings("primaryColor");
        colorPickerSettings("secondaryColor");

        //Initialize list preference
        fontPreferenceSettings("prefSetFont");
        fontPreferenceSettings("prefSetFontStyle");
        fontPreferenceSettings("prefSetFontFace");

        //Initialize Preference
        resetPreferenceSettings("resetDialog");
    }

    public void resetPreferenceSettings(String preferenceKey) {
        this.resetDialogPreference = findPreference(preferenceKey);
        this.startIntent = new Intent(context, UserSettingActivity.class);
        //Set the OnPreferenceChangeListener for the resetDialogPreference
        this.resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Both enter and exit animations are set to zero, so no transition animation is applied
                // userSettingActivity.applyOverrideConfiguration();
                //Call this line, just to make sure that the system doesn't apply an animation
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //Close this Activity
                userSettingActivity.activityFinish();
                //Again, don't set an animation for the transition
                // userSettingActivity.applyOverrideConfiguration();
                startActivity(startIntent);
                return false;
            }
        });
    }

    public void fontPreferenceSettings(String prefSetFont) {
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

    public void colorPickerSettings(String colorPickerKey) {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
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

    public void setColorPickerPreferenceValue(String colorPickerKey) {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
        int color = primaryColorPreference.getValue();
        primaryColorPreference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color))));
    }

    public void setListPreferenceSettingValue(String preferenceSettingKey) {
        ListPreference preferenceFont = (ListPreference) findPreference(preferenceSettingKey);
        if (preferenceFont.getValue() == null) {
            // to ensure we don't get a null value
            // set first value by default
            preferenceFont.setValueIndex(0);
        }
        preferenceFont.setSummary(preferenceFont.getValue().toString());
    }
}
