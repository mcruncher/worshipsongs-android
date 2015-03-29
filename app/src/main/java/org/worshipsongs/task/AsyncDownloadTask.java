package org.worshipsongs.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.utils.PropertyUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class AsyncDownloadTask extends AsyncTask<String, Void, Boolean>
{
    public static final String GET_REQUEST = "GET";
    public static final String DATABASE_UPDATED_DATE_KEY = "databaseUpdatedDateKey";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private File externalCacheDir;
    private File commonPropertyFile;
    private SongDao songDao;

    public AsyncDownloadTask()
    {

    }

    public AsyncDownloadTask(Context context)
    {
        songDao = new SongDao(context);
        commonPropertyFile = PropertyUtils.getCommonPropertyFile(context);
        externalCacheDir = context.getExternalCacheDir();
    }

    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected void onProgressUpdate(Void... values)
    {

    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        File destinationFile = null;
        try {
            String remoteUrl = "https://raw.githubusercontent.com/crunchersaspire/worshipsongs-db/master/songs.sqlite";
            String className = this.getClass().getSimpleName();
            destinationFile =  File.createTempFile("download-songs", "sqlite", externalCacheDir);
            URL url = new URL(remoteUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET_REQUEST);
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(60000);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            DataInputStream dataInputStream = new DataInputStream(urlConnection.getInputStream());
            int contentLength = urlConnection.getContentLength();
            Log.d(className, "Content length " + contentLength);
            byte[] buffer = new byte[contentLength];
            dataInputStream.readFully(buffer);
            dataInputStream.close();
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(destinationFile));
            dataOutputStream.write(buffer);
            dataOutputStream.flush();
            dataOutputStream.close();
            Log.i(className, "Finished downloading file!");
            songDao.copyDatabase(destinationFile.getAbsolutePath(), true);
            songDao.open();
            PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
            return true;
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error", ex);
            return false;
        } finally {
            destinationFile.deleteOnExit();
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {

    }
}
