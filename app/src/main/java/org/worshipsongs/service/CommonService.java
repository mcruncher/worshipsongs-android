package org.worshipsongs.service;

import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Seenivasan on 5/9/2015.
 */
public class CommonService
{

    private Set<String> serviceList;
    private File serviceFile = null;
    private WorshipSongApplication application = new WorshipSongApplication();

    public Set<String> readServiceName()
    {
        Properties property = new Properties();
        InputStream inputStream = null;
        serviceList = new HashSet<String>();
        int i = 0;
        try {
            serviceFile = PropertyUtils.getPropertyFile(application.getContext(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            inputStream = new FileInputStream(serviceFile);
            property.load(inputStream);

            Enumeration<?> e = property.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                //String value = prop.getProperty(key);
                serviceList.add(key);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return serviceList;
    }

    public void saveIntoFile(String serviceName, String song)
    {
        try {
            serviceFile = PropertyUtils.getPropertyFile(application.getContext(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            if (!serviceFile.exists()) {
                FileUtils.touch(serviceFile);
            }
            String existingProperty = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValue = "";
            if (StringUtils.isNotBlank(existingProperty)) {
                if (existingProperty.contains(song)) {
                    propertyValue = existingProperty;
                } else {
                    if (existingProperty.endsWith(";")) {
                        propertyValue = existingProperty + song;
                    } else {
                        propertyValue = existingProperty + ";" + song;
                    }
                }
            } else {
                propertyValue = song;
            }
            Log.e(this.getClass().getName(), "Save into file " + propertyValue);
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }


}
