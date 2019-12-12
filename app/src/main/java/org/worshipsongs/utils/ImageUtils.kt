package org.worshipsongs.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * Author : Madasamy
 * Version : x.x.x
 */

object ImageUtils
{

    fun resizeBitmapImageFn(bitmapSource: Bitmap, maxResolution: Int): Bitmap
    {
        val iWidth = bitmapSource.width
        val iHeight = bitmapSource.height
        var newWidth = iWidth
        var newHeight = iHeight
        var rate = 0.0f

        if (iWidth > iHeight)
        {
            if (maxResolution < iWidth)
            {
                rate = maxResolution / iWidth.toFloat()
                newHeight = (iHeight * rate).toInt()
                newWidth = maxResolution
            }
        } else
        {
            if (maxResolution < iHeight)
            {
                rate = maxResolution / iHeight.toFloat()
                newWidth = (iWidth * rate).toInt()
                newHeight = maxResolution
            }
        }
        return Bitmap.createScaledBitmap(bitmapSource, newWidth, newHeight, true)
    }

    fun resizeBitmapImageFn(resources: Resources, bitmapSource: Bitmap, maxResolution: Int): Drawable
    {
        val iWidth = bitmapSource.width
        val iHeight = bitmapSource.height
        var newWidth = iWidth
        var newHeight = iHeight
        var rate = 0.0f

        if (iWidth > iHeight)
        {
            if (maxResolution < iWidth)
            {
                rate = maxResolution / iWidth.toFloat()
                newHeight = (iHeight * rate).toInt()
                newWidth = maxResolution
            }
        } else
        {
            if (maxResolution < iHeight)
            {
                rate = maxResolution / iHeight.toFloat()
                newWidth = (iWidth * rate).toInt()
                newHeight = maxResolution
            }
        }
        return BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmapSource, newWidth, newHeight, true))
    }
}
