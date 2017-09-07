package org.worshipsongs.preference;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

import org.worshipsongs.activity.DatabaseSettingActivity;

/**
 * Author : Madasamy
 * Version : 3.x.
 */

public class DatabaseSettingsPreference extends Preference
{

    public DatabaseSettingsPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        getContext().startActivity(new Intent(getContext(), DatabaseSettingActivity.class));
    }

}
