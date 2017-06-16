package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;

import java.util.Map;

/**
 * Author: Seenivasan
 * Version: 1.0.0
 */
public class UserPreferenceSettingService
{

    private Context context = WorshipSongApplication.getContext();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    public UserPreferenceSettingService()
    {

    }

    public float getPortraitFontSize()
    {
        return sharedPreferences.getInt(CommonConstants.PRIMARY_FONT_SIZE_KEY, 18);
    }

    public float getLandScapeFontSize()
    {
        return sharedPreferences.getInt(CommonConstants.PRESENTATION_FONT_SIZE_KEY, 25);
    }

    public int getPrimaryColor()
    {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if (all.containsKey("primaryColor")) {
            color = Integer.parseInt(all.get("primaryColor").toString());
        } else {
            color = -12303292;
        }
        return color;
    }

    public int getSecondaryColor()
    {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if (all.containsKey("secondaryColor")) {
            color = Integer.parseInt(all.get("secondaryColor").toString());
        } else {
            color = -65536;
        }
        return color;
    }

    public int getPresentationBackgroundColor()
    {
        return sharedPreferences.getInt("presentationBackgroundColor", 0xff000000);
    }

    public int getPresentationPrimaryColor()
    {
        return sharedPreferences.getInt("presentationPrimaryColor", 0xffffffff);
    }

    public int getPresentationSecondaryColor()
    {
        return sharedPreferences.getInt("presentationSecondaryColor", 0xffffff00);
    }

    public boolean isKeepAwake()
    {
        return sharedPreferences.getBoolean("prefKeepAwakeOn", false);
    }

    public boolean isPlayVideo()
    {
        return sharedPreferences.getBoolean("prefVideoPlay", true);
    }

    public boolean isTamilLyrics()
    {
        return sharedPreferences.getBoolean("displayTamilLyrics", true);
    }

    public boolean isRomanisedLyrics()
    {
        return sharedPreferences.getBoolean("displayRomanisedLyrics", true);
    }

    public boolean isTamil()
    {
        return sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0) == 0;
    }
}
