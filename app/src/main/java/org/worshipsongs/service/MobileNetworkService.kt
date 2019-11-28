package org.worshipsongs.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * author:Madasamy
 * version:1.2.0
 */
class MobileNetworkService : IMobileNetworkService
{
    override fun isWifi(`object`: Any): Boolean
    {
        //object should be system service
        val connectivityManager = `object` as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected &&
                networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    override fun isMobileData(`object`: Any): Boolean
    {
        val connectivityManager = `object` as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected &&
                networkInfo.type == ConnectivityManager.TYPE_MOBILE

    }
}
