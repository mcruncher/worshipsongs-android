package org.worshipsongs.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;

import org.worshipsongs.activity.FontTabBarActivity;
import org.worshipsongs.activity.FontTabFragment;
import org.worshipsongs.worship.R;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontDialogPreference extends Preference
{
    FragmentManager fragmentManager;
    private Activity activity;

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

        //TODO: To change the swipe tab fragment
//        fragmentManager.beginTransaction()
//                .replace(R.id.frame_container, new FontTabFragment()).commit();

    }
}
