package org.worshipsongs.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.helper.DatabaseHelper;
import org.worshipsongs.utils.RegexUtils;

import java.io.IOException;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */

public class DatabaseService
{
    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    public static final String TOPIC_NAME_REGEX = "\\{.*\\}";

    public DatabaseService()
    {
        //Do nothing
    }

    public DatabaseService(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public void copyDatabase(String databasePath, boolean dropDatabase) throws IOException
    {
        databaseHelper.createDataBase(databasePath, dropDatabase);
    }

    public boolean isDatabaseExist()
    {
        return databaseHelper.checkDataBase();
    }

    public void open()
    {
        database = databaseHelper.openDataBase();
    }

    public void close()
    {
        databaseHelper.close();
    }

    public SQLiteDatabase getDatabase()
    {
        if (database == null) {
            database = databaseHelper.openDataBase();
        }
        return database;
    }

   public String parseTamilName(String topicName)
    {
        if (StringUtils.isNotBlank(topicName)) {
            String tamilTopicName = RegexUtils.getMatchString(topicName, TOPIC_NAME_REGEX);
            String formattedTopicName = tamilTopicName.replaceAll("\\{", "").replaceAll("\\}", "");
            return StringUtils.isNotBlank(formattedTopicName) ? formattedTopicName : topicName;
        }
        return "";
    }

   public String parseEnglishName(String topicName)
    {
        if (StringUtils.isNotBlank(topicName)) {
            return topicName.replaceAll(TOPIC_NAME_REGEX, "");
        }
        return "";
    }
}
