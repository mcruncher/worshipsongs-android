package org.worshipsongs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;

import org.worshipsongs.task.AsyncDownloadTask;
import org.worshipsongs.task.AsyncGitHubRepositoryTask;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.Date;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends Activity
{

    public static final String DATABASE_UPDATED_DATE_KEY = "databaseUpdatedDateKey";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final int TIME = 3 * 100;// 4 seconds
    private SongDao songDao;
    private SharedPreferences sharedPreferences;
    private TextView message;
    private AsyncGitHubRepositoryTask asyncGitHubRepositoryTask;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        asyncGitHubRepositoryTask = new AsyncGitHubRepositoryTask(this);
        setContentView(R.layout.splash_screen);
        songDao = new SongDao(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                loadBundleDatabase();
                Intent intent = new Intent(SplashScreenActivity.this,
                        MainActivity.class);
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

    private void loadBundleDatabase()
    {
        try {
            SplashScreenActivity context = SplashScreenActivity.this;
            String projectVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.i(SplashScreenActivity.class.getSimpleName(), "Project version." + projectVersion);
            if (projectVersion.contains("SNAPSHOT") && isWifi()) {
                if (asyncGitHubRepositoryTask.execute().get()) {
                    Log.i(SplashScreenActivity.class.getSimpleName(), "Preparing to copy remote database.");
                    AsyncDownloadTask asyncDownloadTask = new AsyncDownloadTask(context);
                    if (asyncDownloadTask.execute().get()) {
                        Log.i(SplashScreenActivity.class.getSimpleName(), "Remote development database copied successfully.");
                    }
                }
            } else {
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
            }
        } catch (Exception e) {
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

    public long getDaysInBetween(Date startDate, Date endDate)
    {
        if (endDate.getTime() >= startDate.getTime()) {
            long dateDifferenceInMillSeconds = Math.abs(endDate.getTime() - startDate.getTime());
            return (dateDifferenceInMillSeconds / (24 * 60 * 60 * 1000)) + 1;
        }
        return -1;
    }


    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }
}