package org.worshipsongs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;

/**
 * Author : madasamy
 * Version : 2.2.0
 */
public final class CommonUtils
{
    public static void hideKeyboard(Activity activity)
    {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public static boolean isWifiOrMobileDataConnectionExists(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) || (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTablet(Context context)
    {
        return context.getResources().getBoolean(R.bool.tablet);
    }

    public static boolean isPhone(Context context)
    {
        return !context.getResources().getBoolean(R.bool.tablet);
    }

    public static boolean isAboveKitkat()
    {
        return android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
    }

}
