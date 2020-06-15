package org.worshipsongs.service


import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import org.worshipsongs.R

/**
 * @author Madasamy
 * @since  1.x
 */
class ResetDefaultSettingsService(var activityContext: Context, attrs: AttributeSet) :
        DialogPreference(activityContext, attrs)
{
    init
    {
        setTitle(R.string.reset_default_title)
    }

    override fun onClick()
    {
        val dialogBuilder = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.reset_default_title))
        dialogBuilder.setPositiveButton(R.string.ok) { dialog, which ->
            val preferencesEditor = PreferenceManager.getDefaultSharedPreferences(activityContext).edit()
            preferencesEditor.clear()
            PreferenceManager.setDefaultValues(activityContext, R.xml.settings, true)
            preferencesEditor.apply()
            onPreferenceChangeListener.onPreferenceChange(this, true)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.cancel()
        }
        dialogBuilder.show()
    }
}
