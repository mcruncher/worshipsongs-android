package org.worshipsongs.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.worshipsongs.activity.DatabaseSettingActivity;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.worship.R;

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
