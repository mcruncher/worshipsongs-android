package org.worshipsongs.service;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.CommonConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seenivasan on 10/26/2014.
 */
public class CustomTagColorService
{
    private static Pattern pattern;
    private File externalCacheDir;
    private UserPreferenceSettingService preferenceSettingService;
    File customTagFile;

    public void setCustomTagTextView(Context context, String text, TextView textView) {
        preferenceSettingService = new UserPreferenceSettingService();
        externalCacheDir = context.getExternalCacheDir();
        customTagFile = PropertyUtils.getCommonPropertyFile(context);
        List<String> strings;
        strings = getStrings(text);
        String tagKey = null;
        for (int i = 0; i < strings.size(); i++) {
            Matcher matcher = pattern.matcher(strings.get(i));
            if (matcher.find()) {
                String value = matcher.group(0).replace("{", "");
                tagKey = value.replace("}", "");
                if(getTagColor(tagKey) == null){
                    PropertyUtils.appendColoredText(textView, removeTag(strings.get(i), tagKey), Color.BLACK);
                }
                else
                    PropertyUtils.appendColoredText(textView, removeTag(strings.get(i), tagKey), getTagColor(tagKey));
            }
            else
                PropertyUtils.appendColoredText(textView, strings.get(i), preferenceSettingService.getColor());
        }
    }


    private List<String> getStrings(String foo)
    {
        List<String> strings = new ArrayList<String>();
        String[] split = foo.split("\\n");
        pattern = Pattern.compile("\\{\\w\\}");
        Matcher startMatcher = null;
        Pattern endPattern = Pattern.compile("\\{/\\w\\}");
        Matcher endMatcher = null;
        String string;
        for (int i = 0; i < split.length; i++) {
            string = split[i];
            startMatcher = pattern.matcher(string);
            StringBuilder builder = new StringBuilder();
            if (startMatcher.find()) {
                int j = i;
                boolean endMatcherExists = false;
                do {
                    j = j + 1;
                    builder.append(string);
                    //System.out.println("String 1" + string);
                    endMatcher = endPattern.matcher(string);
                    if (endMatcher.find()) {
                        endMatcherExists = true;
                    } else {
                        if (j < split.length) {
                            string = split[j];
                        } else {
                            string = null;
                        }
                        i = j;
                    }
                    if (!endMatcherExists && string != null) {
                        endMatcher = endPattern.matcher(string);
                        if (endMatcher.find()) {
                            endMatcherExists = true;
                            builder.append(string);
                        }
                    }
                } while (!endMatcherExists && string != null);
                strings.add(builder.toString());
            } else {
                strings.add(string);
            }
        }
        return strings;
    }

    private Integer getTagColor(String tagKey)
    {
        Integer colorVal = null;
        try{
            List<String> fileContent = FileUtils.readLines(customTagFile);
            String color = PropertyUtils.getProperty(tagKey, customTagFile);
            if (!color.isEmpty())
                colorVal = Integer.parseInt(color);
            else
                colorVal = Color.BLUE;
        }
        catch (Exception ex){

        }
        return colorVal;
    }

    private String removeTag(String line, String tagKey)
    {
        String content1 = line.replaceAll("\\{", "");
        String content2 = content1.replaceAll("\\}", "");
        String content3 = content2.replaceAll(tagKey, "");
        String content4 = content3.replaceAll("/", "");
        return content4;
    }
}
