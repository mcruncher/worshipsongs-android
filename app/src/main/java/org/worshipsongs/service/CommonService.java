package org.worshipsongs.service;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.SongListAdapter;
import org.worshipsongs.component.DropDownList;
import org.worshipsongs.domain.Song;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Seenivasan on 5/9/2015.
 */
public class CommonService {

    private List<String> serviceList = new ArrayList<String>();
    private File serviceFile = null;
    private WorshipSongApplication application = new WorshipSongApplication();

    public List<String> readServiceName() {
        Properties property = new Properties();
        InputStream inputStream = null;
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

    public void saveIntoFile(String serviceName, String song) {
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
                    propertyValue = existingProperty + ";" + song;
                }
            } else {
                propertyValue = song;
            }
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }


}
