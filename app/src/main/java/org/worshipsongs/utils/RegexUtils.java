package org.worshipsongs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Madasamy
 * Version: 2.4.0
 */
public final class RegexUtils
{
    public static String getMatchString(String text, String regex)
    {
        Pattern pattern1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        // Now create matcher object.
        Matcher matcher = pattern1.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }

    }
}
