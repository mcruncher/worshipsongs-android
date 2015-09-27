package org.worshipsongs.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.worshipsongs.CommonConstants;
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
import java.util.Date;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends AppCompatActivity
{

    public static final String GET_REQUEST = "GET";
    public static final String DATABASE_UPDATED_DATE_KEY = "databaseUpdatedDateKey";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private SongDao songDao;
    private SharedPreferences sharedPreferences;
    private AsyncGitHubRepositoryTask asyncGitHubRepositoryTask;
    private ProgressBar progressBar;
    private File commonPropertyFile;
    private File externalCacheDir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().hide();
        asyncGitHubRepositoryTask = new AsyncGitHubRepositoryTask(this);
        setContentView(R.layout.splash_screen);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        songDao = new SongDao(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        commonPropertyFile = PropertyUtils.getPropertyFile(this, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
        externalCacheDir = getExternalCacheDir();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadDatabase();
    }

    private void loadDatabase()
    {
        try {
            SplashScreenActivity context = SplashScreenActivity.this;
            String projectVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.i(SplashScreenActivity.class.getSimpleName(), "Project version " + projectVersion);
            if (projectVersion.contains("SNAPSHOT") && isWifi()) {
                if (asyncGitHubRepositoryTask.execute().get()) {
                    Log.i(SplashScreenActivity.class.getSimpleName(), "Preparing to copy remote database....");
                    new AsyncDownloadTask(context, projectVersion).execute();
                } else {
                    moveToMainActivity();
                }
            } else {
                copyBundleDatabase(context, projectVersion);
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while loading database");
        }
    }


    public final boolean isWifi()
    {
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

    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }

    public class AsyncDownloadTask extends AsyncTask<String, Void, Boolean>
    {
        private String projectVersion;

        public AsyncDownloadTask()
        {

        }

        public AsyncDownloadTask(Context context, String projectVersion)
        {
            this.projectVersion = projectVersion;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            File destinationFile = null;
            try {
                String remoteUrl = "https://raw.githubusercontent.com/crunchersaspire/worshipsongs-db-dev/master/songs.sqlite";
                String className = SplashScreenActivity.this.getClass().getSimpleName();
                destinationFile = File.createTempFile("download-songs", "sqlite", externalCacheDir);
                URL url = new URL(remoteUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(GET_REQUEST);
                urlConnection.setConnectTimeout(60000);
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
        protected void onPostExecute(Boolean successfull)
        {
            if (successfull) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Remote development database copied successfully.");
                moveToMainActivity();
            } else {
                copyBundleDatabase(SplashScreenActivity.this, projectVersion);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void copyBundleDatabase(SplashScreenActivity context, String projectVersion)
    {
        try {
            File commonPropertyFile = PropertyUtils.getPropertyFile(context, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
            String existingVersion = PropertyUtils.getProperty(CommonConstants.VERSION_KEY, commonPropertyFile);
            Log.i(SplashScreenActivity.class.getSimpleName(), "Project version in property file" + existingVersion);
            if (StringUtils.isNotBlank(existingVersion) && existingVersion.equalsIgnoreCase(projectVersion)) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Bundle database already copied.");
            } else {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Preparing to copy bundle database.");
                songDao.copyDatabase("", true);
                songDao.open();
                PropertyUtils.setProperty(CommonConstants.VERSION_KEY, projectVersion, commonPropertyFile);
                Log.i(SplashScreenActivity.class.getSimpleName(), "Bundle database copied successfully.");
            }
            moveToMainActivity();
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while coping databases", ex);
        }
    }

    void moveToMainActivity()
    {
        Intent intent = new Intent(SplashScreenActivity.this, NavigationDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
        SplashScreenActivity.this.finish();
    }
}