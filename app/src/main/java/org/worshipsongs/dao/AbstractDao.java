package org.worshipsongs.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.worshipsongs.helper.DatabaseHelper;

import java.io.IOException;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */

public class AbstractDao
{
    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private Context context;

    public AbstractDao()
    {
        //Do nothing
    }

    public AbstractDao(Context context)
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

    protected SQLiteDatabase getDatabase()
    {
        if (database == null) {
            database = databaseHelper.openDataBase();
        }
        return database;
    }
}
