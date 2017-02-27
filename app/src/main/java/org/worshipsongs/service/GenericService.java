package org.worshipsongs.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.helper.DatabaseHelper;

import java.io.IOException;

/**
 * Author : Madasamy
 * Version : 2.x
 */

public class GenericService<T>
{
//    private SQLiteDatabase database;
//    private DatabaseHelper databaseHelper;
//
//    public GenericService()
//    {
//        databaseHelper = new DatabaseHelper(WorshipSongApplication.getContext());
//    }
//
//    public GenericService(Context context)
//    {
//        databaseHelper = new DatabaseHelper(context);
//    }
//
//    public void copyDatabase(String databasePath, boolean dropDatabase) throws IOException
//    {
//        databaseHelper.createDataBase(databasePath, dropDatabase);
//    }
//
//    public boolean isDatabaseExist()
//    {
//        return databaseHelper.checkDataBase();
//    }
//
//    public void open()
//    {
//        database = databaseHelper.openDataBase();
//    }
//
//    public void close()
//    {
//        databaseHelper.close();
//    }
//
//    protected SQLiteDatabase getDatabase()
//    {
//        if (database == null) {
//            database = databaseHelper.openDataBase();
//        }
//        return database;
//    }

}
