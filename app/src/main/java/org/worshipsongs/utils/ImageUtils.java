package org.worshipsongs.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class ImageUtils
{

    public static Bitmap resizeBitmapImageFn(Bitmap bitmapSource, int maxResolution)
    {
        int iWidth = bitmapSource.getWidth();
        int iHeight = bitmapSource.getHeight();
        int newWidth = iWidth;
        int newHeight = iHeight;
        float rate = 0.0f;

        if (iWidth > iHeight) {
            if (maxResolution < iWidth) {
                rate = maxResolution / (float) iWidth;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        } else {
            if (maxResolution < iHeight) {
                rate = maxResolution / (float) iHeight;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }
        return Bitmap.createScaledBitmap(bitmapSource, newWidth, newHeight, true);
    }

    public static Drawable resizeBitmapImageFn(Resources resources, Bitmap bitmapSource, int maxResolution)
    {
        int iWidth = bitmapSource.getWidth();
        int iHeight = bitmapSource.getHeight();
        int newWidth = iWidth;
        int newHeight = iHeight;
        float rate = 0.0f;

        if (iWidth > iHeight) {
            if (maxResolution < iWidth) {
                rate = maxResolution / (float) iWidth;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        } else {
            if (maxResolution < iHeight) {
                rate = maxResolution / (float) iHeight;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }
        return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmapSource, newWidth, newHeight, true));
    }
}
