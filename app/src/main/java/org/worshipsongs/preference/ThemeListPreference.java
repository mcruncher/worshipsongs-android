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
    private SharedPreferences sharedPreferences;
    private PreferenceListener preferenceListener;

    public ThemeListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setEntries(new String[]{context.getString(R.string.light), context.getString(R.string.dark) });
        setEntryValues(new String[]{Theme.DAY.name(), Theme.NIGHT.name()});
        setDefaultValue(Theme.DAY.name());
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);
        if (which == DialogInterface.BUTTON_POSITIVE) {
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply();
            preferenceListener.onSelect();
        }
    }

    public void setPreferenceListener(PreferenceListener preferenceListener)
    {
        this.preferenceListener = preferenceListener;
    }
}
