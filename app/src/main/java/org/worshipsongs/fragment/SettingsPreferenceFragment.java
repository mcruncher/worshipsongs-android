package org.worshipsongs.fragment;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.widget.Toast;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.adapter.SongCardViewAdapter;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.picker.ColorPickerPreference;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

/**
 * Author:Seenivasan, Madasamy
 * version:1.0.0
 */
public class SettingsPreferenceFragment extends PreferenceFragment
{
    private UserSettingActivity userSettingActivity = new UserSettingActivity();
    private final SparseArray<SettingsPreferenceFragment.RemotePresentation> activePresentations = new SparseArray<SettingsPreferenceFragment.RemotePresentation>();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        colorPickerSettings("primaryColor");
        colorPickerSettings("secondaryColor");
       // setRemoteDisplayPreference();
        resetPreferenceSettings("resetDialog");
    }

//    private void setRemoteDisplayPreference()
//    {
//        final Preference presentSongPreference = (Preference) findPreference("prefPresentSong");
//        presentSongPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//        {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//            public boolean onPreferenceClick(Preference preference)
//            {
//                //open browser or intent here
//                SwitchPreference switchPreference = (SwitchPreference) preference;
//                if (switchPreference.isChecked()) {
//                    Display display = getDisplay();
//                    if (display != null) {
//                        Setting.getInstance().setDisplay(display);
//                        showPresentation();
//                        Toast.makeText(WorshipSongApplication.getContext(), "Tap song content to present a song in " + display.getName() + " display", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Setting.getInstance().setDisplay(null);
//                        preference.getEditor().putBoolean("prefPresentSong", false);
//                        preference.getEditor().apply();
//                        switchPreference.setChecked(false);
//                        Toast.makeText(WorshipSongApplication.getContext(), "Remote display not connected to this device", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    if (Setting.getInstance().getDisplay() != null) {
//                        hidePresentation(Setting.getInstance().getDisplay());
//                    }
//                }
//                return true;
//            }
//        });
//    }
//
//    private Display getDisplay()
//    {
//        Log.i(SongCardViewAdapter.class.getSimpleName(), "Is jelly bean " + isJellyBean());
//        if (isJellyBean()) {
//            DisplayManager displayManager = (DisplayManager) getActivity().getSystemService(Context.DISPLAY_SERVICE);
//            Display[] displays = displayManager.getDisplays();
//            Log.i(SongCardViewAdapter.class.getSimpleName(), "No of displays" + displays.length);
//            for (Display display : displays) {
//                Log.i(SongCardViewAdapter.class.getSimpleName(), "Display name " + display.getName());
//                if (!display.getName().contains("Built-in Screen")) {
//                    return display;
//                }
//            }
//        }
//        return null;
//    }

    private boolean isJellyBean()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

//    public void showPresentation()
//    {
//        if (isJellyBean()) {
//            RemotePresentation presentation = new RemotePresentation(Setting.getInstance().getDisplay());
//            activePresentations.put(Setting.getInstance().getDisplay().getDisplayId(), presentation);
//            presentation.show();
//        }
//    }
//
//    public void hidePresentation(Display display)
//    {
//        if (isJellyBean()) {
//            final int displayId = display.getDisplayId();
//            RemotePresentation presentation = activePresentations.get(displayId);
//            if (presentation == null) {
//                return;
//            }
//            presentation.dismiss();
//            activePresentations.delete(displayId);
//        }
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private final class RemotePresentation extends Presentation
    {

        RemotePresentation(Display display)
        {
            super(getActivity(), display);

        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash_screen);

        }
    }

}