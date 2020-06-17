package org.worshipsongs.service

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.View
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import org.worshipsongs.dialog.RemoteSongPresentation
import org.worshipsongs.domain.Setting
import org.worshipsongs.domain.Song
import org.worshipsongs.utils.CommonUtils

/**
 * @author: Madasamy
 * @version: 3.x
 */

class PresentationScreenService
{
    private val preferenceSettingService = UserPreferenceSettingService()
    private var context: Context? = null
    var presentation: RemoteSongPresentation? = null
    private var songMediaRouterCallBack = DefaultMediaRouterCallBack()
    private var mediaRouter: MediaRouter? = null

    private val remoteDisplayDismissListener = DialogInterface.OnDismissListener { dialog ->
        if (dialog == presentation)
        {
            presentation = null
        }
    }

    private val isJellyBean: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(context: Context)
    {
        this.context = context
        mediaRouter = MediaRouter.getInstance(context)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private inner class DefaultMediaRouterCallBack : MediaRouter.Callback()
    {

        override fun onRouteSelected(router: MediaRouter, info: MediaRouter.RouteInfo)
        {
            updatePresentation()

        }

        override fun onRouteUnselected(router: MediaRouter, info: MediaRouter.RouteInfo)
        {
            updatePresentation()

        }

        override fun onRoutePresentationDisplayChanged(router: MediaRouter, info: MediaRouter.RouteInfo)
        {
            updatePresentation()
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun updatePresentation()
    {
        try
        {
            val selectedRoute = mediaRouter!!.getSelectedRoute()
            val selectedDisplay = selectedRoute.presentationDisplay
            if (presentation != null && presentation!!.display != selectedDisplay)
            {
                if (presentation!!.isShowing)
                {
                    presentation!!.dismiss()
                }
                presentation = null
            }
            Log.i(this.javaClass.simpleName, "Presentation: $presentation")
            if (presentation == null && selectedDisplay != null)
            {
                presentation = RemoteSongPresentation(context!!, selectedDisplay)
                presentation!!.setOnDismissListener(remoteDisplayDismissListener)
                presentation!!.show()
            }
            if (presentation != null && selectedDisplay != null)
            {
                if (Setting.instance.song != null)
                {
                    showNextVerse(Setting.instance.song, Setting.instance.slidePosition)
                }
            }
        } catch (ex: Exception)
        {
            presentation = null
            Log.e(PresentationScreenService::class.java.simpleName, "Error occurred while presenting remote display", ex)
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun onResume()
    {
        if (isJellyBean && CommonUtils.isProductionMode)
        {
            mediaRouter!!.addCallback(MediaRouteSelector.Builder().addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO).build(), songMediaRouterCallBack)
            updatePresentation()
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun onPause()
    {
        if (isJellyBean && CommonUtils.isProductionMode)
        {
            mediaRouter!!.removeCallback(songMediaRouterCallBack!!)
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun onStop()
    {
        try
        {
            if (presentation != null)
            {
                presentation!!.cancel()
                presentation = null
            }
        } catch (ex: Exception)
        {
            Log.e(PresentationScreenService::class.java.simpleName, "Error occurred while dismiss remote display", ex)
        }

    }

    fun showNextVerse(song: Song?, position: Int)
    {
        try
        {
            if (presentation != null)
            {
                presentation!!.setVerseVisibility(View.VISIBLE)
                presentation!!.setImageViewVisibility(View.GONE)
                presentation!!.setVerse(song!!.contents!![position])
                presentation!!.setSongTitleAndChord(song.title!!, song.chord!!, preferenceSettingService.presentationPrimaryColor)
                presentation!!.setAuthorName(song.authorName!!, preferenceSettingService.presentationPrimaryColor)
                presentation!!.setSlidePosition(position, song.contents!!.size, preferenceSettingService.presentationPrimaryColor)
                Setting.instance.song = song
                Setting.instance.slidePosition = position
            }
        } catch (e: Exception)
        {
            Log.e(PresentationScreenService::class.java.simpleName, "Error occurred while presenting song content$e")
        }
    }

}
