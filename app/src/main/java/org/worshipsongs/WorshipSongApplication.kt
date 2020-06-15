package org.worshipsongs

import android.app.Application
import android.content.Context

/**
 * Created by Seenivasan on 10/19/2014.
 */
class WorshipSongApplication : Application()
{

    override fun onCreate()
    {
        super.onCreate()
        context = applicationContext
    }

    companion object
    {
        var context: Context? = null
    }
}
