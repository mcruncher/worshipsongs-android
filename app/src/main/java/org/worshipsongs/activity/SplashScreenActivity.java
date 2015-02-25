package org.worshipsongs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.worship.R;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.task.AsyncGitHubRepositoryTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends Activity
{
    private static final int TIME = 4 * 1000;// 4 seconds
    private SongDao songDao;
    TextView messageAlert;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        songDao = new SongDao(this);
        progressDialog = new ProgressDialog(SplashScreenActivity.this);
        messageAlert = (TextView)findViewById(R.id.message);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
              loadRemoteDatabase();
                Intent intent = new Intent(SplashScreenActivity.this,
                        SongsViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                SplashScreenActivity.this.finish();
            }
        }, TIME);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
            }
        }, TIME);
    }

    private void loadRemoteDatabase()
    {
        try {

            if (isInternetOn()) {
                AsyncGitHubRepositoryTask asyncGitHubRepositoryTask = new AsyncGitHubRepositoryTask(this);
                if (asyncGitHubRepositoryTask.execute().get()) {
                    Log.i(this.getClass().getName(), "Preparing to load database...");
                    final AsyncRemoteDownloadTask asyncDownloadTask = new AsyncRemoteDownloadTask();
                    String remoteUrl = "https://raw.githubusercontent.com/crunchersaspire/worshipsongs-db/master/songs.sqlite";
                    File externalCacheDir = this.getExternalCacheDir();
                    File downloadSongFile = null;
                    messageAlert.setText("Downloading latest songs database...");
                    try {
                        downloadSongFile = File.createTempFile("download-songs", "sqlite", externalCacheDir);
                        Log.i(this.getClass().getName(), "Download file from " + remoteUrl + " to" + downloadSongFile.getAbsolutePath());
                        if (asyncDownloadTask.execute(remoteUrl, downloadSongFile.getAbsolutePath()).get()) {
                            songDao.copyDatabase(downloadSongFile.getAbsolutePath(), true);
                            songDao.open();
                            Log.i(this.getClass().getName(), "Copied successfully");
                        } else {
                            Log.w(SettingsActivity.class.getSimpleName(), "File is not downloaded from " + remoteUrl);
                        }
                    } catch (Exception e) {
                        Log.e(SettingsActivity.class.getSimpleName(), "Error occurred while downloading file" + e);
                    } finally {
                        downloadSongFile.deleteOnExit();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public final boolean isInternetOn()
    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        } else if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            //Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }

    private class AsyncRemoteDownloadTask extends AsyncTask<String, Void, Boolean>
    {
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
                urlConnection.setConnectTimeout(60000);
                urlConnection.setReadTimeout(120000);
                urlConnection.setRequestMethod("GET");
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
    }
}