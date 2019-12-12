package org.worshipsongs.preference

import android.content.Context

import android.util.AttributeSet
import androidx.preference.ListPreference
import org.worshipsongs.R
import org.worshipsongs.domain.Theme

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ThemeListPreference(context: Context, attrs: AttributeSet) : ListPreference(context, attrs)
{

    init
    {
        entries = arrayOf(context.getString(R.string.light), context.getString(R.string.dark))
        entryValues = arrayOf(Theme.DAY.name, Theme.NIGHT.name)
        setDefaultValue(Theme.DAY.name)
    }

}
