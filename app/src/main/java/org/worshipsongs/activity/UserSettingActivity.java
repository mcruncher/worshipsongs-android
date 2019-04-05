package org.worshipsongs.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.fragment.SettingsPreferenceFragment;
import org.worshipsongs.service.PresentationScreenService;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class UserSettingActivity extends AbstractAppCompactActivity
{
    private ActionBar actionBar;
    private PresentationScreenService presentationScreenService;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.settings);
        SettingsPreferenceFragment settingsPreferenceFragment = new SettingsPreferenceFragment();
        settingsPreferenceFragment.setUserSettingActivity(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, settingsPreferenceFragment).commit();
        presentationScreenService = new PresentationScreenService(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        activityFinish();
        if (sharedPreferences.getBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, false)) {
            NavUtils.navigateUpFromSameTask(this);
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, false).apply();
        }
    }

    public void activityFinish()
    {
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }
}

