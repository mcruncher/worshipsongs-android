package org.worshipsongs.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import org.worshipsongs.WorshipSongApplication;

import java.util.Map;

/**
 * Created by Seenivasan on 10/23/2014.
 */
public class UserPreferenceSettingService
{

    private Context context = WorshipSongApplication.getContext();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    private Typeface fontFaceStyle;
    private float fontSize;
    private Typeface fontStyle;
    private String color;

    public UserPreferenceSettingService()
    {
    }

    public float getFontSize()
    {
        return sharedPreferences.getInt("fontSize", 10);
    }

    public int getFontStyle()
    {
        String sharedTypeFace = sharedPreferences.getString("prefSetFontStyle", "NULL");
        int fontStyle = 0;
        if (sharedTypeFace.equals("BOLD"))
            fontStyle = Typeface.BOLD;
        if (sharedTypeFace.equals("BOLD_ITALIC"))
            fontStyle = Typeface.BOLD_ITALIC;
        if (sharedTypeFace.equals("ITALIC"))
            fontStyle = Typeface.ITALIC;
        if (sharedTypeFace.equals("NORMAL"))
            fontStyle = Typeface.NORMAL;
        return fontStyle;
    }

    public int getColor()
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

    public Integer getTagColor()
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

    public boolean getKeepAwakeStatus()
    {
        return sharedPreferences.getBoolean("prefKeepAwakeOn", false);
    }
}
