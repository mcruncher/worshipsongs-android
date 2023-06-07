package org.worshipsongs.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication

/**
 * Author : madasamy
 * Version : 2.2.0
 */
object CommonUtils
{

    val isProductionMode: Boolean
        get() {
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.context)
            return defaultSharedPreferences.getBoolean("production", true)
        }

    val isLollipopOrGreater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    val isAboveKitkat: Boolean
        get() = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT

    val isAboveOreo: Boolean
        get() = Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1

    val projectVersion: String
        get() {
            var version = ""
            try {

                version = WorshipSongApplication.context!!.packageManager.getPackageInfo(WorshipSongApplication.context!!.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException)
            {
                Log.e("CommonUtils", "Error occurred while finding version")
            }

            return version
        }

    val isNotImportedDatabase: Boolean
        get()
        {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.context)
            return !sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false)
        }

    fun hideKeyboard(activity: Activity?)
    {
        if (activity != null)
        {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null)
            {
                view = View(activity)
            }
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isNewVersion(versionInPropertyFile: String, currentVersion: String): Boolean
    {
        try
        {
            return !(StringUtils.isNotBlank(versionInPropertyFile) && versionInPropertyFile.equals(currentVersion, ignoreCase = true))
        } catch (ex: Exception)
        {
            return false
        }

    }

    fun isWifiOrMobileDataConnectionExists(context: Context): Boolean
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null)
        {
            if (networkInfo.isConnected)
            {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
                {
                    return true
                }
            }
        }
        return false
    }

    fun isTablet(context: Context): Boolean
    {
        return context.resources.getBoolean(R.bool.tablet)
    }

    fun isPhone(context: Context): Boolean
    {
        return !context.resources.getBoolean(R.bool.tablet)
    }

}
