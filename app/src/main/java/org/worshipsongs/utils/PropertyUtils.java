package org.worshipsongs.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * author:Seenivasan
 * author:Madasamy
 * version:1.0.0
 */
public final class PropertyUtils
{
    private static final String CLASS_NAME = PropertyUtils.class.getSimpleName();

    public static File getPropertyFile(Context context, String propertyFileName)
    {
        File commonPropertyFile = null;
        try {
            String configDirPath = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/config";
            File configDir = new File(configDirPath);
            commonPropertyFile = new File(configDir, propertyFileName);
            if (!commonPropertyFile.exists()) {
                FileUtils.touch(commonPropertyFile);
            }
        } catch (Exception ex) {
            Log.e("PropertyUtils", "Error" + ex);
        }
        return commonPropertyFile;
    }

    public static Properties getProperties(File propertyFile)
    {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertyFile);
            properties.load(inputStream);
            return properties;
        } catch (Exception ex) {
            return properties;
        }
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

    public static void setProperties(Map<String, String> propertiesMap, File propertiesFile)
    {
        Properties properties = getProperties(propertiesFile);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(propertiesFile);
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

    public static void removeProperty(String key, File propertiesFile)
    {
        try {
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
            fileInputStream.close();
            if (properties.remove(key) != null) {
                FileOutputStream out = new FileOutputStream(propertiesFile);
                properties.store(out, "");
                out.close();
            }
        } catch (Exception ex) {
        }
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

    public static List<String> getServices(File propertyFileName)
    {
        List<String> services = new ArrayList<>();
        InputStream inputStream = null;
        try {
            Properties property = new Properties();
            inputStream = new FileInputStream(propertyFileName);
            property.load(inputStream);
            Enumeration<?> enumeration = property.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                services.add(key);
            }
            inputStream.close();

        } catch (Exception ex) {
            Log.e(PropertyUtils.class.getSimpleName(), "Error occurred while reading services", ex);
        }
        return services;
    }

    public static boolean removeSong(File propertyFile, String serviceName, String song)
    {
        try {
            String propertyValue = "";
            System.out.println("Preparing to remove service:" + song);
            String property = PropertyUtils.getProperty(serviceName, propertyFile);
            String propertyValues[] = property.split(";");
            System.out.println("File:" + propertyFile.getAbsolutePath());
            for (int i = 0; i < propertyValues.length; i++) {
                System.out.println("Property length: " + propertyValues.length);
                Log.i(CLASS_NAME, "Property value  " + propertyValues[i]);
                if (StringUtils.isNotBlank(propertyValues[i]) && !propertyValues[i].equalsIgnoreCase(song)) {
                    Log.i(CLASS_NAME, "Append property value" + propertyValues[i]);
                    propertyValue = propertyValue + propertyValues[i] + ";";
                }
            }
            Log.i(CLASS_NAME, "Property value after removed  " + propertyValue);
            PropertyUtils.setProperty(serviceName, propertyValue, propertyFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
