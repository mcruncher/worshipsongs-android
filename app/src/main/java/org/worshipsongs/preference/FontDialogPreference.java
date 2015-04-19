package org.worshipsongs.preference;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

import org.worshipsongs.activity.FontTabBarActivity;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontDialogPreference extends Preference
{
    public FontDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(true);
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        Intent intent = new Intent(getContext().getApplicationContext(), FontTabBarActivity.class);
        setIntent(intent);
    }
}
