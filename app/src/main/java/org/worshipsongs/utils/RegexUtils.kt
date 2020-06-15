package org.worshipsongs.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Author: Madasamy
 * Version: 2.4.0
 */
object RegexUtils
{
    fun getMatchString(text: String, regex: String): String
    {
        val pattern1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        // Now create matcher object.
        val matcher = pattern1.matcher(text)
        return if (matcher.find())
        {
            matcher.group()
        } else
        {
            ""
        }

    }
}
