package org.worshipsongs.preference

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference

import org.worshipsongs.activity.DatabaseSettingActivity

/**
 * @author : Madasamy
 * @version : 3.x.
 */

class DatabaseSettingsPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs)
{

    override fun onClick()
    {
        super.onClick()
        context.startActivity(Intent(context, DatabaseSettingActivity::class.java))
    }

}
