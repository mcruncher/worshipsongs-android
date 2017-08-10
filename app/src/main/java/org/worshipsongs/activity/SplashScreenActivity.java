package org.worshipsongs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.service.ISongService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.Locale;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends AppCompatActivity
{

    private static final String LANGUAGE_CHOOSED_KEY = "languageChoosedKey";
    private ISongService songService;
    private SharedPreferences sharedPreferences;

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
        songService = new SongService(context);
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
                songService.copyDatabase("", true);
                songService.open();
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
        int index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0);
        if (!languageChoosed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SplashScreenActivity.this, R.style.MyDialogTheme));
            String[] languageList = new String[]{getString(R.string.tamil_key), getString(R.string.english_key)};
            builder.setSingleChoiceItems(languageList, index, getOnItemClickListener());
            builder.setPositiveButton(R.string.ok, getOkButtonClickListener());
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View titleView = inflater.inflate(R.layout.dialog_custom_title, null);
            builder.setCustomTitle(titleView);
            builder.setCancelable(false);
            builder.show();
        } else {
            String localeCode = index == 0 ? "ta" : "en";
            setLocale(new Locale(localeCode));
            moveToMainActivity();
        }
    }

    @NonNull
    private DialogInterface.OnClickListener getOnItemClickListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, which).apply();
            }
        };
    }

    @NonNull
    private DialogInterface.OnClickListener getOkButtonClickListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sharedPreferences.edit().putBoolean(LANGUAGE_CHOOSED_KEY, true).apply();
                String localeCode = which == 0 ? "ta" : "en";
                setLocale(new Locale(localeCode));
                dialog.cancel();
                moveToMainActivity();
            }
        };
    }

    private void setLocale(Locale configureLocale)
    {
        Locale.setDefault(configureLocale);
        Configuration config = new Configuration();
        config.locale = configureLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void moveToMainActivity()
    {
        Intent intent = new Intent(SplashScreenActivity.this, NavigationDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
        SplashScreenActivity.this.finish();
    }
}