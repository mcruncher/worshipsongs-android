package org.worshipsongs.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.service.PresentationScreenService;

/**
 * Author : Madasamy
 * Version : 3.0.x
 */

public class FavouriteSongsHelpActivity extends AppCompatActivity
{
    private PresentationScreenService presentationScreenService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presentationScreenService = new PresentationScreenService(FavouriteSongsHelpActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FavouriteSongsHelpActivity.this);
        setContentView(R.layout.favourite_songs_help_activity);
    }

    public void onClickOk(View view)
    {
        finish();
        saveHelpFavouritePreference();
    }

    public void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        saveHelpFavouritePreference();
    }

    private void saveHelpFavouritePreference()
    {
        sharedPreferences.edit().putBoolean(CommonConstants.DISPLAY_FAVOURITE_HELP_ACTIVITY, true).apply();
    }
}
