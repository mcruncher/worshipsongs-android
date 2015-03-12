package org.worshipsongs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.task.AsyncGitHubRepositoryTask;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends Activity {

    public static final String DATABASE_UPDATED_DATE_KEY = "databaseUpdatedDateKey";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final int TIME = 4 * 1000;// 4 seconds
    private SongDao songDao;
    TextView messageAlert;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        songDao = new SongDao(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        progressDialog = new ProgressDialog(SplashScreenActivity.this);
        messageAlert = (TextView) findViewById(R.id.message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRemoteDatabase();
                Intent intent = new Intent(SplashScreenActivity.this,
                        SongsViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                SplashScreenActivity.this.finish();
            }
        }, TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, TIME);
    }

    private void loadRemoteDatabase() {
        try {
            File commonPropertyFile = PropertyUtils.getCommonPropertyFile(this);
            String lastDatabaseUpdatedDateString = PropertyUtils.getProperty(DATABASE_UPDATED_DATE_KEY, commonPropertyFile);
            if (StringUtils.isBlank(lastDatabaseUpdatedDateString)) {
                songDao.copyDatabase("", true);
                songDao.open();
                PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
            } else {
                if (isDownloadDatabaseUpdates(commonPropertyFile)) {
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
                }
            }
        } catch (Exception e) {
        }
    }

    public final boolean isInternetOn() {
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

    private boolean isDownloadDatabaseUpdates(File commonPropertyFile) {
        try {
            Log.i(SplashScreenActivity.class.getSimpleName(), "Preparing to download database updates ...");
            String databaseUpdateInterval = sharedPreferences.getString("databaseUpdateInterval", "MONTHLY");
            Log.i(SplashScreenActivity.class.getSimpleName(), "Database base update interval: " + databaseUpdateInterval);
            if (databaseUpdateInterval.equalsIgnoreCase("STARTUP")) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Updates download every startup");
                return true;
            } else {
                //File commonPropertyFile = PropertyUtils.getCommonPropertyFile(this);
                String lastDatabaseUpdatedDateString = PropertyUtils.getProperty(DATABASE_UPDATED_DATE_KEY, commonPropertyFile);
                Log.i(SplashScreenActivity.class.getSimpleName(), "Finally database updated date: " + lastDatabaseUpdatedDateString);
                if (StringUtils.isNotBlank(lastDatabaseUpdatedDateString)) {
                    Date lastDatabaseUpdatedDate = DateUtils.parseDate(lastDatabaseUpdatedDateString, new String[]{DATE_PATTERN});
                    Log.i(SplashScreenActivity.class.getSimpleName(), "Finally database updated date: " + lastDatabaseUpdatedDateString);
                    long daysInBetween = getDaysInBetween(lastDatabaseUpdatedDate, new Date());
                    if (databaseUpdateInterval.equalsIgnoreCase("daily") && daysInBetween >= 2) {
                        PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
                        Log.i(SplashScreenActivity.class.getSimpleName(), "Updates download daily");
                        return true;
                    } else if (databaseUpdateInterval.equalsIgnoreCase("weekly") && daysInBetween >= 7) {
                        PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
                        Log.i(SplashScreenActivity.class.getSimpleName(), "Updates download weekly");
                        return true;
                    } else if (databaseUpdateInterval.equalsIgnoreCase("monthly") && daysInBetween >= 30) {
                        Log.i(SplashScreenActivity.class.getSimpleName(), "Updates download monthly");
                        PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
                        return true;
                    } else {
                        Log.i(SplashScreenActivity.class.getSimpleName(), "System does not reach download update interval");
                    }
                } else {
                    Log.i(SplashScreenActivity.class.getSimpleName(), "Application open first time it should download updates");
                    PropertyUtils.setProperty(DATABASE_UPDATED_DATE_KEY, DateFormatUtils.format(new Date(), DATE_PATTERN), commonPropertyFile);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(SplashScreenActivity.class.getSimpleName(), "Error occurred" + e);
        }
        return false;
    }

    /**
     * <p>Returns the days between the two given dates including the start day.</p>
     * NOTE: The start date should be lesser than the end date.
     * <p>Examples:</p>
     * <ul>
     * <li>1-Sep-2013, 10-Sep-2013 = 10</li>
     * <li>10-Sep-2013, 10-Sep-2013 = 1</li>
     * <li>11-Sep-2013, 10-Sep-2013 = -1</li>
     * </ul>
     *
     * @param startDate
     * @param endDate
     * @return
     */

    public long getDaysInBetween(Date startDate, Date endDate) {
        if (endDate.getTime() >= startDate.getTime()) {
            long dateDifferenceInMillSeconds = Math.abs(endDate.getTime() - startDate.getTime());
            return (dateDifferenceInMillSeconds / (24 * 60 * 60 * 1000)) + 1;
        }
        return -1;
    }


    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private class AsyncRemoteDownloadTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
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