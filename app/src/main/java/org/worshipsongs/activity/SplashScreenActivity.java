package org.worshipsongs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.domain.DragDrop;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.service.DatabaseService;
import org.worshipsongs.service.FavouriteService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SplashScreenActivity extends AppCompatActivity
{

    private DatabaseService databaseService;
    private SharedPreferences sharedPreferences;
    private FavouriteService favouriteService;
    private SongService songService;
    private String favouriteName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initSetUp(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        migrateFavourites();
        importFavourites();
        loadDatabase();
    }

    private void importFavourites()
    {
        Uri data = getIntent().getData();

        if (data != null) {
            String encodedString = data.getQuery();
            String decodedString = new String(Base64.decode(encodedString, 0));
            String[] favouriteIdArray = decodedString.split(";");
            if (favouriteIdArray != null && favouriteIdArray.length > 0) {
                favouriteName = favouriteIdArray[0];
                List<SongDragDrop> songDragDrops = new ArrayList<>();
                for (int i = 1; i < favouriteIdArray.length; i++) {
                    Song song = songService.findById(Integer.valueOf(favouriteIdArray[i]));
                    SongDragDrop songDragDrop = new SongDragDrop(song.getId(), song.getTitle(), false);
                    songDragDrop.setTamilTitle(song.getTamilTitle());
                    songDragDrops.add(songDragDrop);
                }
                favouriteService.save(favouriteName, songDragDrops);
                Log.i(SplashScreenActivity.class.getSimpleName(), favouriteName +
                        " successfully imported with " + songDragDrops.size() + " songs");
            }
        }
    }

    void initSetUp(Context context)
    {
        databaseService = new DatabaseService(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        favouriteService = new FavouriteService();
        songService = new SongService(context);
    }

    private void migrateFavourites()
    {
        if (sharedPreferences.getBoolean(CommonConstants.MIGRATION_KEY, true)) {
            favouriteService.migration(this);
            sharedPreferences.edit().putBoolean(CommonConstants.MIGRATION_KEY, false).apply();
        }
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
                databaseService.copyDatabase("", true);
                databaseService.open();
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
        boolean languageChoosed = sharedPreferences.getBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, false);
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
                setLocale();
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
                sharedPreferences.edit().putBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, true).apply();
                dialog.cancel();
                moveToMainActivity();
            }
        };
    }

    private void moveToMainActivity()
    {
        setLocale();
        Intent intent = new Intent(SplashScreenActivity.this, NavigationDrawerActivity.class);
        intent.putExtra(CommonConstants.FAVOURITES_KEY, favouriteName);
        startActivity(intent);
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
        SplashScreenActivity.this.finish();
    }

    private void setLocale()
    {
        int index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0);
        String localeCode = (index == 0) ? "ta" : "en";
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}