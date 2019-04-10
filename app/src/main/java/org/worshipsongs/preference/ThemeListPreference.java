package org.worshipsongs.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.domain.Theme;

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
public class ThemeListPreference extends ListPreference
{

    public ThemeListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setEntries(new String[]{context.getString(R.string.light), context.getString(R.string.dark) });
        setEntryValues(new String[]{Theme.DAY.name(), Theme.NIGHT.name()});
        setDefaultValue(Theme.DAY.name());
    }

}
