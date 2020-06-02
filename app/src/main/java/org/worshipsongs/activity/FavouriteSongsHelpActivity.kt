package org.worshipsongs.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.service.PresentationScreenService

/**
 * Author : Madasamy
 * Version : 3.0.x
 */

class FavouriteSongsHelpActivity : AppCompatActivity()
{
    private var presentationScreenService: PresentationScreenService? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        presentationScreenService = PresentationScreenService(this@FavouriteSongsHelpActivity)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@FavouriteSongsHelpActivity)
        setContentView(R.layout.favourite_songs_help_activity)
    }

    fun onClickOk(view: View)
    {
        finish()
        saveHelpFavouritePreference()
    }

    public override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }

    override fun onStop()
    {
        super.onStop()
        presentationScreenService!!.onStop()
    }

    override fun onPause()
    {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        finish()
        saveHelpFavouritePreference()
    }

    private fun saveHelpFavouritePreference()
    {
        sharedPreferences!!.edit().putBoolean(CommonConstants.DISPLAY_FAVOURITE_HELP_ACTIVITY, true).apply()
    }
}
