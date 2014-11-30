package org.worshipsongs.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.CommonConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Seenivasan on 10/26/2014.
 */
public final class PropertyUtils
{

    public static void setProperties(Map<String, String> propertiesMap, File propertiesFile)
    {
        Properties properties = new Properties();
        OutputStream outputStream = null;
        try {

            outputStream = new FileOutputStream(propertiesFile, true);
            for (String key : propertiesMap.keySet()) {
                properties.setProperty(key, propertiesMap.get(key));
            }
            properties.store(outputStream, "");
        } catch (Exception ex) {

        }
    }

    public static void setProperty(String key, String value, File propertiesFile)
    {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put(key, value);
        setProperties(propertyMap, propertiesFile);
    }

    public static String getProperty(String key, File propertiesFile)
    {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);
            return properties.get(key).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static File getCommonPropertyFile(Context context)
    {
        File commonPropertyFile = null;
        try {
            String configDirPath = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/config";
            File configDir = new File(configDirPath);
            commonPropertyFile = new File(configDir, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
            if (!commonPropertyFile.exists()) {
                FileUtils.touch(commonPropertyFile);
            }
        } catch (Exception ex) {
            Log.e("PropertyUtils", "Error" + ex);
        }
        return commonPropertyFile;
    }

    public static void appendColoredText(TextView tv, String text, int color)
    {
        int start = tv.getText().length();
        tv.append(text);
        tv.append("\n");
        int end = tv.getText().length();
        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }
}
