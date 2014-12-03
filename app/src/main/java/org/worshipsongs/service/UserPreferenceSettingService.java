package org.worshipsongs.service;

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
public class UserPreferenceSettingService{

    Context context = WorshipSongApplication.getContext();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    private Typeface typeFace;
    private Typeface fontFaceStyle;
    private float fontSize;
    private Typeface fontStyle;
    private String color;

    public UserPreferenceSettingService(){
    }


    public Typeface getTypeFace() {
        String sharedTypeFace = sharedPreferences.getString("prefSetFontFace","NULL");
        if(sharedTypeFace.equals("DEFAULT"))
            typeFace=Typeface.DEFAULT;
        if(sharedTypeFace.equals("DEFAULT_BOLD"))
            typeFace=Typeface.DEFAULT_BOLD;
        if(sharedTypeFace.equals("MONOSPACE"))
            typeFace=Typeface.MONOSPACE;
        if(sharedTypeFace.equals("SANS_SERIF"))
            typeFace=Typeface.SANS_SERIF;
        if(sharedTypeFace.equals("SERIF"))
            typeFace=Typeface.SERIF;
        return typeFace;
    }

    public float getFontSize() {
        String sharedFontSize = sharedPreferences.getString("prefSetFont","NULL");
        if(sharedFontSize.equals("SMALL"))
            fontSize=10;
        if(sharedFontSize.equals("MEDIUM"))
            fontSize=15;
        if(sharedFontSize.equals("NORMAL"))
            fontSize=20;
        if(sharedFontSize.equals("HIGH"))
            fontSize=30;
        return fontSize;
    }

    public int getFontStyle() {
        String sharedTypeFace = sharedPreferences.getString("prefSetFontStyle","NULL");
        int fontStyle=0;
        if(sharedTypeFace.equals("BOLD"))
            fontStyle=Typeface.BOLD;
        if(sharedTypeFace.equals("BOLD_ITALIC"))
            fontStyle=Typeface.BOLD_ITALIC;
        if(sharedTypeFace.equals("ITALIC"))
            fontStyle=Typeface.ITALIC;
        if(sharedTypeFace.equals("NORMAL"))
            fontStyle=Typeface.NORMAL;
        return fontStyle;
    }

    public int getColor() {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if(all.containsKey("primaryColor")){
            color= Integer.parseInt(all.get("primaryColor").toString());
        }
        else{
            color=-12303292;
        }
        return color;
    }

    public Integer getTagColor()
    {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if(all.containsKey("secondaryColor")){
            color= Integer.parseInt(all.get("secondaryColor").toString());
        }
        else{
            color=-12303292;
        }
        return color;
    }

    public boolean getKeepAwakeStatus(){
        return sharedPreferences.getBoolean("prefKeepAwakeOn", false);
    }
}
