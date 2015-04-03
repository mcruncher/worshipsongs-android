package org.worshipsongs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;

import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends Activity {

    public static final String DATABASE_UPDATED_DATE_KEY = "databaseUpdatedDateKey";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final int TIME = 3 * 100;// 4 seconds
    private SongDao songDao;
    TextView messageAlert;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private TextView message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        songDao = new SongDao(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        progressDialog = new ProgressDialog(SplashScreenActivity.this);
        messageAlert = (TextView) findViewById(R.id.message);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        messageAlert.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRemoteDatabase();
                Intent intent = new Intent(SplashScreenActivity.this,
                        MainActivity.class);
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
                
            }
        } catch (Exception e) {
        }
    }

    public final boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                    return true;
                }
            }
        }
        Log.i(UserSettingActivity.class.getSimpleName(), "System does not connect with wifi");
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
                    long daysInBetween = getDaysInBetween(lastDatabaseUpdatedDate, new Date());
                    if (databaseUpdateInterval.equalsIgnoreCase("daily") && daysInBetween >= 2) {
                        return true;
                    } else if (databaseUpdateInterval.equalsIgnoreCase("weekly") && daysInBetween >= 7) {
                        return true;
                    } else if (databaseUpdateInterval.equalsIgnoreCase("monthly") && daysInBetween >= 30) {
                        return true;
                    } else {
                        Log.i(SplashScreenActivity.class.getSimpleName(), "System does not reach download update interval");
                    }
                } else {
                    Log.i(SplashScreenActivity.class.getSimpleName(), "Application open first time it should download updates");
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