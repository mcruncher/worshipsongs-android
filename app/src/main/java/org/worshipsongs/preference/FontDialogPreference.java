package org.worshipsongs.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.worshipsongs.activity.FontTabBarActivity;
import org.worshipsongs.activity.MainActivity;
import org.worshipsongs.worship.R;

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
