package org.worshipsongs.helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.activity.NavigationDrawerActivity;

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
    private static final int DATA_BASE_VERSION = 3;
    public static String dbPath = "";

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
        super(context, dbName, null, DATA_BASE_VERSION);
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
            this.close();
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
        try {
            return new File(dbPath, dbName).exists();
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error occurred while checking database" + ex);
            return false;
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    public void copyDataBase(String databasePath) throws IOException
    {
        InputStream inputStream = null;
        // Open local db as the input stream
        if (StringUtils.isNotBlank(databasePath)) {
            inputStream = new FileInputStream(new File(databasePath));
        } else {
            inputStream = context.getAssets().open(dbName);
        }
        String outFileName = dbPath + dbName;
        OutputStream outputStream = new FileOutputStream(outFileName);
        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        // Close the streams
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException
    {
        //Open the database
        String myPath = dbPath + dbName;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return database;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    @Override
    public synchronized void close()
    {
        if (database != null) {
            database.close();
        }
        SQLiteDatabase.releaseMemory();
        super.close();
    }

    public SQLiteDatabase getDatabase()
    {
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Do nothing its read only database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Do nothing its read only database
    }

}
