package org.worshipsongs.service;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import org.worshipsongs.utils.PropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seenivasan on 10/26/2014.
 */
public class CustomTagColorService
{
    private static Pattern pattern = Pattern.compile("\\{\\w\\}");
    private UserPreferenceSettingService preferenceSettingService;

    public void setCustomTagTextView(Context context, String text, TextView textView)
    {
        preferenceSettingService = new UserPreferenceSettingService();
        List<String> strings = getStringsByTag(text);
        String tagKey = null;
        for (int i = 0; i < strings.size(); i++) {
            Matcher matcher = pattern.matcher(strings.get(i));
            if (matcher.find()) {
                String value = matcher.group(0).replace("{", "");
                tagKey = value.replace("}", "");
                if (preferenceSettingService.getTagColor() == null) {
                    PropertyUtils.appendColoredText(textView, removeTag(strings.get(i), tagKey), Color.BLACK);
                } else {
                    PropertyUtils.appendColoredText(textView, removeTag(strings.get(i), tagKey), preferenceSettingService.getTagColor());
                }
            } else {
                PropertyUtils.appendColoredText(textView, strings.get(i), preferenceSettingService.getColor());
            }
        }
    }

    public List<String> getFormattedLines(String content)
    {
        List<String> lyricsListWithTag = getStringsByTag(content);
        List<String> lyricsList = new ArrayList<>();
        String tagKey = null;
        for (int i = 0; i < lyricsListWithTag.size(); i++) {
            Matcher matcher = pattern.matcher(lyricsListWithTag.get(i));
            if (matcher.find()) {
                String value = matcher.group(0).replace("{", "");
                tagKey = value.replace("}", "");
                lyricsList.add(removeTag(lyricsListWithTag.get(i), tagKey));
            } else {
                lyricsList.add(lyricsListWithTag.get(i));
            }
        }
        return lyricsList;
    }

    private List<String> getStringsByTag(String songContent)
    {
        List<String> strings = new ArrayList<String>();
        String[] split = songContent.split("\\n");
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

    String removeTag(String line, String tagKey)
    {
        String firstRemovePattern = "\\{" + tagKey + "\\}";
        String secondRemovePattern = "\\{/" + tagKey + "\\}";
        String replacedWithFirstPattern = line.replaceAll(firstRemovePattern, "");
        String replacedWithSecondPattern = replacedWithFirstPattern.replaceAll(secondRemovePattern, "");
        return replacedWithSecondPattern;
    }
}
