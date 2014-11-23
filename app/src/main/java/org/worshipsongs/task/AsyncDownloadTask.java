package org.worshipsongs.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class AsyncDownloadTask extends AsyncTask<String, Void, Boolean>
{
    public static final String GET_REQUEST = "GET";
    private ProgressDialog progressDialog;

    public AsyncDownloadTask()
    {

    }

    public AsyncDownloadTask(Context context)
    {
        progressDialog = new ProgressDialog(context);
    }



    @Override
    protected void onPreExecute()
    {

        Log.d(this.getClass().getName(), "Preparing to show dialog" + progressDialog);
        if (progressDialog != null) {
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            Log.d(this.getClass().getName(), "Displayed the progress bar");
        }
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        try {

            String remoteUrl = strings[0];
            String destinationPath = strings[1];
            String className = this.getClass().getSimpleName();
            Log.i(className, "Preparing to download " + destinationPath + " from " + remoteUrl);
            File destinationFile = new File(destinationPath);
            URL url = new URL(remoteUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(GET_REQUEST);
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
            return true;
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error", ex);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
