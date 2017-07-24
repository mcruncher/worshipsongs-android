package org.worshipsongs.service;

import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private Boolean tagExists = false;

    public void setCustomTagTextView(TextView textView, String text, int primaryColor, int secondaryColor)
    {
        tagExists = pattern.matcher(text).find();
        List<String> strings = getStringsByTag(text);
        String tagKey = null;

        for (int i = 0; i < strings.size(); i++) {
            Matcher matcher = pattern.matcher(strings.get(i));
            if (matcher.find()) {
                String value = matcher.group(0).replace("{", "");
                tagKey = value.replace("}", "");
                if (displayTamilLyrics() || !displayRomanisedLyrics()) {
                    setColoredTextView(textView, removeTag(strings.get(i), tagKey), secondaryColor);
                }
            } else {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || !tagExists) {
                    setColoredTextView(textView, strings.get(i), primaryColor);
                }
            }
        }
    }

    protected void setColoredTextView(TextView textView, String content, int color) {
        PropertyUtils.appendColoredText(textView, content, color);
    }

    protected boolean displayTamilLyrics() {
        return preferenceSettingService.isTamilLyrics();
    }

    protected boolean displayRomanisedLyrics() {
        return preferenceSettingService.isRomanisedLyrics();
    }

    public String getFormattedLines(String content)
    {
        tagExists = pattern.matcher(content).find();
        StringBuilder textView = new StringBuilder();
        List<String> lyricsListWithTag = getStringsByTag(content);
        String tagKey = null;
        for (int i = 0; i < lyricsListWithTag.size(); i++) {
            Matcher matcher = pattern.matcher(lyricsListWithTag.get(i));
            if (matcher.find()) {
                if (displayTamilLyrics() || !displayRomanisedLyrics()) {
                    String value = matcher.group(0).replace("{", "");
                    tagKey = value.replace("}", "");
                    textView.append(removeTag(lyricsListWithTag.get(i), tagKey));
                    textView.append("\n");
                }
            } else {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || !tagExists) {
                    textView.append(lyricsListWithTag.get(i));
                    textView.append("\n");
                }
            }
        }
        return textView.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public float getFormattedPage(String content, PdfDocument.Page page, float x, float y)
    {
        tagExists = pattern.matcher(content).find();
        List<String> lyricsListWithTag = getStringsByTag(content);
        String tagKey = null;
        for (int i = 0; i < lyricsListWithTag.size(); i++) {
            Matcher matcher = pattern.matcher(lyricsListWithTag.get(i));
            if (matcher.find()) {
                if (displayTamilLyrics() || !displayRomanisedLyrics()) {
                    String value = matcher.group(0).replace("{", "");
                    tagKey = value.replace("}", "");
                    page.getCanvas().drawText(removeTag(lyricsListWithTag.get(i), tagKey) + "\n", x, y, new Paint());
                    y = y + 20;
                }
            } else {
                if (displayRomanisedLyrics() || !displayTamilLyrics() || !tagExists) {
                    page.getCanvas().drawText(lyricsListWithTag.get(i) + "\n", x, y, new Paint());
                    y = y + 20;
                }
            }
        }
        return y;
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
