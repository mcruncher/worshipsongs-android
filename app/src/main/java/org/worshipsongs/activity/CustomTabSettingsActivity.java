package org.worshipsongs.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.worship.R;
import org.worshipsongs.picker.ColorPickerPreference;

import java.io.File;
import java.util.Map;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class CustomTabSettingsActivity extends PreferenceActivity
{
    private Context context = WorshipSongApplication.getContext();
    private File customTagFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                        Map<String, ?> all = sp.getAll();
                        String tagName = "";
                        int color = 0;
                        if (all.containsKey("color1")) {
                            color = Integer.parseInt(all.get("color1").toString());
                        }
                        if (all.containsKey("prefTagName"))
                            tagName = all.get("prefTagName").toString();
                        saveIntoFile(tagName, color);
                        Toast.makeText(CustomTabSettingsActivity.this, "Added into properties file" + customTagFile, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // "Cancel"
                        Toast.makeText(CustomTabSettingsActivity.this, "Cancel Clicked ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
// Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
// END_INCLUDE (inflate_set_custom_view)
        setContentView(R.layout.activity_done_bar);
    }

    private void saveIntoFile(String tagName, int color)
    {
        try {
            File externalCacheDir = context.getExternalCacheDir();
            customTagFile = new File(externalCacheDir, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
            if (!customTagFile.exists()) {
                FileUtils.touch(customTagFile);
            }
            PropertyUtils.setProperty(tagName, String.valueOf(color), customTagFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        Context context = WorshipSongApplication.getContext();

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.custom_tab_settings);
            ((ColorPickerPreference) findPreference("color1")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    Log.d(CustomTabSettingsActivity.class.getName(), "Font Color:" + ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                    return true;
                }
            });
            ((ColorPickerPreference) findPreference("color1")).setAlphaSliderEnabled(true);
        }
    }
}
