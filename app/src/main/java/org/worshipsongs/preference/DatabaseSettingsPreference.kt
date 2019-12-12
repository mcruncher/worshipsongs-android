package org.worshipsongs.preference

import android.content.Context
import android.content.Intent
import android.preference.Preference
import android.util.AttributeSet

import org.worshipsongs.activity.DatabaseSettingActivity

/**
 * Author : Madasamy
 * Version : 3.x.
 */

class DatabaseSettingsPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs)
{

    override fun onClick()
    {
        super.onClick()
        context.startActivity(Intent(context, DatabaseSettingActivity::class.java))
    }

}
