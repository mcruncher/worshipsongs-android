package org.worshipsongs.helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static String dbPath = "";
    private static final int dataBaseVersion = 3;
    public static String dbName = "songs.sqlite";
    private SQLiteDatabase database;
    private final Context context;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context, dbName, null, dataBaseVersion);
        this.context = context;
        this.dbPath = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase(String databasePath, boolean dropDatabase) throws IOException
    {
        Log.d(this.getClass().getName(), "Preparing to create database");
        if (dropDatabase) {
            context.deleteDatabase(dbName);
        }
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //do nothing - database already exist
            Log.d(this.getClass().getName(), "Database " + dbName + " already exists");
        } else {
            Log.d(this.getClass().getName(), "Database " + dbName + " is not exists");
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase(databasePath);
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), "Error occurred while copy database " + ex);
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        try {
            String databasePath = dbPath + dbName;
            Log.d(this.getClass().getName(), "Database path" + databasePath);
            checkDB = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error occurred while checking database" + ex);
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    public void copyDataBase(String databasePath) throws IOException
    {
        Log.i(this.getClass().getName(), "Preparing to copy database");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (StringUtils.isNotBlank(databasePath)) {
                inputStream = new FileInputStream(new File(databasePath));
            } else {
                inputStream = context.getAssets().open(dbName);
            }
            String outFileName = dbPath + dbName;
            Log.i(this.getClass().getName(), "Db path: " + outFileName);
            Log.i(this.getClass().getName(), "InputStream : " + inputStream);
            outputStream = new FileOutputStream(outFileName);

            Log.i(this.getClass().getName(), "Output stream: " + outputStream);
            IOUtils.copy(inputStream, outputStream);
            Log.i(this.getClass().getName(), "Copied successfully");
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error occurred while copying database " + ex);
        } finally {
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }

    public SQLiteDatabase openDataBase() throws SQLException
    {
        //Open the database
        String myPath = dbPath + dbName;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return database;
    }

    @Override
    public synchronized void close()
    {
        if (database != null)
            database.close();

        super.close();
    }

    public SQLiteDatabase getDatabase()
    {
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
}
