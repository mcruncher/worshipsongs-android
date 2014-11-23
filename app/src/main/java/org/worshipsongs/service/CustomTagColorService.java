package org.worshipsongs.service;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.CommonConstants;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seenivasan on 10/26/2014.
 */
public class CustomTagColorService
{

    private UserPreferenceSettingService preferenceSettingService;

    public void setCustomTagTextView(Context context, String text, TextView textView)
    {
        preferenceSettingService = new UserPreferenceSettingService();
        String lines[] = text.split("\\r?\\n");
        Pattern r = Pattern.compile(CommonConstants.TAG_PATTERN);
        for (int i = 0; i < lines.length; i++) {
            Log.d(this.getClass().getName(), "Line" + lines[i]);
            Matcher m = r.matcher(lines[i]);
            if (m.find()) {
                String value = m.group(0).replace("{", "");
                String tagKey = value.replace("}", "");

                try {
                    File externalCacheDir = context.getExternalCacheDir();
                    File customTagFile = new File(externalCacheDir, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
                    String color = PropertyUtils.getProperty(tagKey, customTagFile);
                    Log.d(this.getClass().getName(), "Tag Color" + color);
                    String content = lines[i];
                    String content1 = content.replaceAll("\\{", "");
                    Log.d(this.getClass().getName(), "Tag Content1: " + content1);
                    String content2 = content1.replaceAll("\\}", "");
                    Log.d(this.getClass().getName(), "Tag Content2: " + content2);
                    String content3 = content2.replaceAll(tagKey, "");
                    Log.d(this.getClass().getName(), "Tag Content3: " + content3);
                    String content4 = content3.replaceAll("/", "");
                    Log.d(this.getClass().getName(), "Tag Content" + content4);
                    if (!color.isEmpty())
                        PropertyUtils.appendColoredText(textView, content4, Integer.parseInt(color));
                    else
                        PropertyUtils.appendColoredText(textView, content4, Color.BLACK);
                } catch (Exception ex) {
                    Log.d(this.getClass().getName(), "Exception" + ex);
                }
            } else
                PropertyUtils.appendColoredText(textView, lines[i], preferenceSettingService.getColor());
        }
    }
}
