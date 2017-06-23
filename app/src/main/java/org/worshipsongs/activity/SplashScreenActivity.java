package org.worshipsongs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends AppCompatActivity
{

    private static final String LANGUAGE_CHOOSED_KEY = "languageChoosed";
    private SongDao songDao;
    private SharedPreferences sharedPreferences;
    // private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initSetUp(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadDatabase();
    }

    void initSetUp(Context context)
    {
        songDao = new SongDao(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    private void loadDatabase()
    {
        try {
            SplashScreenActivity context = SplashScreenActivity.this;
            String currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.i(SplashScreenActivity.class.getSimpleName(), "Current  version " + currentVersion);
            copyBundleDatabase(context, currentVersion);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while loading database");
        }
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }

    private void copyBundleDatabase(SplashScreenActivity context, String currentVersion)
    {
        try {
            File commonPropertyFile = PropertyUtils.getPropertyFile(context, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
            String versionInPropertyFile = PropertyUtils.getProperty(CommonConstants.VERSION_KEY, commonPropertyFile);
            Log.i(SplashScreenActivity.class.getSimpleName(), "Version in property file " + versionInPropertyFile);
            if (CommonUtils.isNotImportedDatabase() && CommonUtils.isNewVersion(versionInPropertyFile, currentVersion)) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Preparing to copy bundle database.");
                songDao.copyDatabase("", true);
                songDao.open();
                PropertyUtils.setProperty(CommonConstants.VERSION_KEY, currentVersion, commonPropertyFile);
                Log.i(SplashScreenActivity.class.getSimpleName(), "Bundle database copied successfully.");
            }
            showLanguageSelectionDialog();
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while coping databases", ex);
        }
    }

    private void showLanguageSelectionDialog()
    {
        boolean languageChoosed = sharedPreferences.getBoolean(LANGUAGE_CHOOSED_KEY, false);
        if (!languageChoosed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SplashScreenActivity.this, R.style.MyDialogTheme));
            builder.setTitle("Language");
            String[] languageList = new String[]{"Tamil", "English"};
            int index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0);
            builder.setSingleChoiceItems(languageList, index, getDialogListener());
            builder.setPositiveButton(R.string.ok, getPositiveListener());
            builder.show();
        } else {
            moveToMainActivity();
        }
    }

    @NonNull
    private DialogInterface.OnClickListener getDialogListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, which).apply();
                sharedPreferences.edit().putBoolean(LANGUAGE_CHOOSED_KEY, true).apply();

            }
        };
    }

    @NonNull
    private DialogInterface.OnClickListener getPositiveListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                moveToMainActivity();
            }
        };
    }

    private void moveToMainActivity()
    {
        Intent intent = new Intent(SplashScreenActivity.this, NavigationDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
        SplashScreenActivity.this.finish();
    }
}