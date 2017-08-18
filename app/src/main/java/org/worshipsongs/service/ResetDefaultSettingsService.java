package org.worshipsongs.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import org.worshipsongs.R;

/**
 * Author : Seenivasan, Madasamy
 * Version : 1.x
 */
public class ResetDefaultSettingsService extends DialogPreference
{
    protected Context context;

    public ResetDefaultSettingsService(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        setTitle(R.string.reset_default_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            preferencesEditor.clear();
            PreferenceManager.setDefaultValues(context, R.xml.settings, true);
            preferencesEditor.apply();
            getOnPreferenceChangeListener().onPreferenceChange(this, true);
        }
    }
}
