package org.worshipsongs.utils;

import android.content.Context
import java.io.File
import java.util.ArrayList

object LiveShareUtils
{
    fun getServiceDirPath(context: Context): String
    {
        return "/data/data/" + context!!.applicationContext.packageName + "/databases/service/"
    }

    fun getServices(serviceDirPath: String): MutableList<String>
    {
        var services: MutableList<String> = ArrayList()
        val serviceDir = File(serviceDirPath)
        if (serviceDir.exists())
        {
            for (serviceFile in serviceDir.listFiles())
            {
                services.add(serviceFile.name)
            }
        }
        return services
    }

    fun formatLiveShareUrl(url: String): String
    {
        var formattedUrl = url;
        if (formattedUrl.endsWith("0"))
        {
            formattedUrl = formattedUrl.substring(0, formattedUrl.lastIndexOf("=") + 1)
            formattedUrl += "1"
        }
        return formattedUrl;
    }
}
