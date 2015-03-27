package org.worshipsongs.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author:Madasamy
 * version:1.2.0
 */
public class MobileNetworkService implements IMobileNetworkService
{
    @Override
    public boolean isWifi(Object object)
    {
        //object should be system service
        ConnectivityManager connectivityManager = (ConnectivityManager) object;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMobileData(Object object)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) object;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    return true;
                }
            }
        }
        return false;
    }
}
