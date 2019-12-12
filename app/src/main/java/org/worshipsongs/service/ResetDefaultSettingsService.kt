package org.worshipsongs.service

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.util.AttributeSet

import org.worshipsongs.R

/**
 * Author : Seenivasan, Madasamy
 * Version : 1.x
 */
class ResetDefaultSettingsService(var activityContext: Context, attrs: AttributeSet) : DialogPreference(activityContext, attrs)
{

    init
    {
        setTitle(R.string.reset_default_title)
    }

    override fun onClick(dialog: DialogInterface, which: Int)
    {
        super.onClick(dialog, which)
        if (which == DialogInterface.BUTTON_POSITIVE)
        {
            val preferencesEditor = PreferenceManager.getDefaultSharedPreferences(activityContext).edit()
            preferencesEditor.clear()
            PreferenceManager.setDefaultValues(activityContext, R.xml.settings, true)
            preferencesEditor.apply()
            onPreferenceChangeListener.onPreferenceChange(this, true)
        }
    }
}
