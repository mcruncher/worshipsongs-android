package org.worshipsongs.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.fragment.AlertDialogFragment
import org.worshipsongs.task.HttpAsyncTask
import org.worshipsongs.utils.CommonUtils

/**
 * Author : Madasamy
 * Version : 3.x
 */

class UpdateSongsDatabaseActivity : AppCompatActivity(), AlertDialogFragment.DialogListener
{

    companion object
    {
        private val DB_API_URL = "https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/refs/heads/master"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.update_song_database_layout)
        updateSongDatabase()
    }

    private fun hideStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16)
        {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else
        {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun updateSongDatabase()
    {
        if (CommonUtils.isWifiOrMobileDataConnectionExists(this))
        {
            HttpAsyncTask(this).execute(DB_API_URL)
        } else
        {
            val bundle = Bundle()
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning))
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_warning_internet_connection))
            val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
            alertDialogFragment.setVisibleNegativeButton(false)
            alertDialogFragment.setDialogListener(this)
            alertDialogFragment.show(supportFragmentManager, AlertDialogFragment::class.java.simpleName)
        }
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        finish()
    }

    override fun onClickPositiveButton(bundle: Bundle, tag: String)
    {
        finish()
    }

    override fun onClickNegativeButton()
    {
        //Do nothing when click negative button
    }


}
