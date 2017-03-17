package org.worshipsongs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SplashScreenActivity;

/**
 * Author : madasamy
 * Version : 2.2.0
 */
public final class CommonUtils
{
    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isProductionMode()
    {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        return defaultSharedPreferences.getBoolean("production", true);
    }

    public static boolean isJellyBeanMrOrGreater()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isLollipopOrGreater()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getProjectVersion()
    {
        String version = "";
        try {
            version = WorshipSongApplication.getContext().getPackageManager().getPackageInfo(
                    WorshipSongApplication.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CommonUtils", "Error occurred while finding version");
        }
        return version;
    }

   public static boolean isNotImportedDatabase()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        return !(sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false));
    }

   public static boolean isNewVersion(String versionInPropertyFile, String currentVersion)
    {
        try {
            return !(StringUtils.isNotBlank(versionInPropertyFile) && versionInPropertyFile.equalsIgnoreCase(currentVersion));
        } catch (Exception ex) {
            return false;
        }
    }


}
