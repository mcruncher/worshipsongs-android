package org.worshipsongs.activity


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NavUtils
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.fragment.SettingsPreferenceFragment
import org.worshipsongs.service.PresentationScreenService

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
class UserSettingActivity : AbstractAppCompactActivity()
{
    private var actionBar: ActionBar? = null
    private var presentationScreenService: PresentationScreenService? = null
    private var sharedPreferences: SharedPreferences? = null

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar!!.setTitle(R.string.settings)
        val settingsPreferenceFragment = SettingsPreferenceFragment()
        settingsPreferenceFragment.setUserSettingActivity(this)
        fragmentManager.beginTransaction().replace(android.R.id.content, settingsPreferenceFragment).commit()
        presentationScreenService = PresentationScreenService(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            android.R.id.home -> onBackPressed()
            else ->
            {
            }
        }
        return true
    }

    override fun onBackPressed()
    {
        activityFinish()
        if (sharedPreferences!!.getBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, false))
        {
            NavUtils.navigateUpFromSameTask(this)
            sharedPreferences!!.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, false).apply()
        }
    }

    fun activityFinish()
    {
        finish()
    }

    override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }

    override fun onPause()
    {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onStop()
    {
        super.onStop()
        presentationScreenService!!.onStop()
    }

    fun setPresentationScreenService(presentationScreenService: PresentationScreenService)
    {
        this.presentationScreenService = presentationScreenService
    }
}

