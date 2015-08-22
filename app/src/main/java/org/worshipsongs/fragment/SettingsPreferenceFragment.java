package org.worshipsongs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import android.preference.PreferenceFragment;
import android.util.Log;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.picker.ColorPickerPreference;
import org.worshipsongs.preference.FontDialogPreference;
import org.worshipsongs.worship.R;

/**
 * Author:Seenivasan, Madasamy
 * version:1.0.0
 */
public class SettingsPreferenceFragment extends PreferenceFragment
{

    private Preference resetDialogPreference;
    private Intent startIntent;
    private Context context = WorshipSongApplication.getContext();
    UserSettingActivity userSettingActivity = new UserSettingActivity();


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //Initialize Color Picker preference
        colorPickerSettings("primaryColor");
        colorPickerSettings("secondaryColor");

        //Initialize list preference
//        fontPreferenceSettings("prefSetFont");
//        fontPreferenceSettings("prefSetFontStyle");
        //customFontSizepreferenceSetting("customFontSize");
        //Initialize Preference
        resetPreferenceSettings("resetDialog");
    }



    private void customFontSizepreferenceSetting(String customFontSize)
    {
        final FontDialogPreference fontDialogPreference = (FontDialogPreference) findPreference(customFontSize);
        Log.i(this.getClass().getSimpleName(), "Preparing to find font size");
        SharedPreferences fontSizePreference = getActivity().getSharedPreferences("fontSizePreference", Activity.MODE_MULTI_PROCESS);
        fontDialogPreference.setSummary(fontSizePreference.getInt("fontSize", 0));
    }

    public void resetPreferenceSettings(String preferenceKey)
    {
        this.resetDialogPreference = findPreference(preferenceKey);
        this.startIntent = new Intent(context, UserSettingActivity.class);
        //Set the OnPreferenceChangeListener for the resetDialogPreference
        this.resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                //Both enter and exit animations are set to zero, so no transition animation is applied
                // userSettingActivity.applyOverrideConfiguration();
                //Call this line, just to make sure that the system doesn't apply an animation
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //Close this Activity
                userSettingActivity.activityFinish();
                //Again, don't set an animation for the transition
                // userSettingActivity.applyOverrideConfiguration();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new SettingsPreferenceFragment()).commit();
                return false;
            }
        });
    }

//    public void fontPreferenceSettings(String prefSetFont)
//    {
//        ListPreference preferenceFont = (ListPreference) findPreference(prefSetFont);
//        setListPreferenceSettingValue(prefSetFont);
//        preferenceFont.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//        {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue)
//            {
//                preference.setSummary(newValue.toString());
//                return true;
//            }
//        });
//    }

    public void colorPickerSettings(String colorPickerKey)
    {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
        setColorPickerPreferenceValue(colorPickerKey);
        ((ColorPickerPreference) findPreference(colorPickerKey)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                //preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                return true;
            }

        });
        ((ColorPickerPreference) findPreference(colorPickerKey)).setAlphaSliderEnabled(true);
    }

    public void setColorPickerPreferenceValue(String colorPickerKey)
    {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
        int color = primaryColorPreference.getValue();
        //primaryColorPreference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color))));
    }


//    public void setListPreferenceSettingValue(String preferenceSettingKey)
//    {
//        ListPreference preferenceFont = (ListPreference) findPreference(preferenceSettingKey);
//        if (preferenceFont.getValue() == null) {
//            // to ensure we don't get a null value
//            // set first value by default
//            preferenceFont.setValueIndex(0);
//        }
//        preferenceFont.setSummary(preferenceFont.getValue().toString());
//    }
}