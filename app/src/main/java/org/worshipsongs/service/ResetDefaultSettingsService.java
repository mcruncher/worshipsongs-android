package org.worshipsongs.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import org.worshipsongs.worship.R;

/**
 * Created by Seenivasan on 12/4/2014.
 */
public class ResetDefaultSettingsService extends DialogPreference
{
    //An object that will store the Activity's context
    protected Context context;

    public ResetDefaultSettingsService(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //Store the calling Activity's context
        this.context = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);

        //If the 'positive' button has been pressed
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            //Get this application SharedPreferences editor
            SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            //Clear all the saved preference values.
            preferencesEditor.clear();
            //Read the default values and set them as the current values.
            PreferenceManager.setDefaultValues(context, R.xml.settings, true);
            //Commit all changes.
            preferencesEditor.commit();

            //Call this method to trigger the execution of the setOnPreferenceChangeListener() method at the PrefsActivity
            getOnPreferenceChangeListener().onPreferenceChange(this, true);
        }
    }
}
