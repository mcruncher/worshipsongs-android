package org.worshipsongs.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;

import org.worshipsongs.worship.R;

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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
            if (!songDao.isDatabaseExist()) {
                songDao.copyDatabase("", true);
                songDao.open();
            }else{
                Log.i(this.getClass().getSimpleName(), "Database already exists");
            }
        } catch (Exception e) {
        }
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
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }
}