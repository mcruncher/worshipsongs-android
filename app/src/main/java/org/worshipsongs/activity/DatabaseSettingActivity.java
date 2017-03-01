package org.worshipsongs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.CustomDialogBuilder;
import org.worshipsongs.domain.DialogConfiguration;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class DatabaseSettingActivity extends AppCompatActivity
{
    private IImportDatabaseLocator importDatabaseLocator = new ImportDatabaseLocator();
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    private PresentationScreenService presentationScreenService;
    private Button defaultDatabaseButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_layout);
        setActionBar();
        setImportDatabaseButton();
        setDefaultDatabaseButton();
        setResultTextView();
        presentationScreenService = new PresentationScreenService(this);
    }

    private void setActionBar()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.database);
    }

    private void setImportDatabaseButton()
    {
        Button importDatabaseButton = (Button) findViewById(R.id.upload_database_button);
        importDatabaseButton.setOnClickListener(new ImportDatabaseOnClickListener());
    }

    private class ImportDatabaseOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            showDatabaseTypeDialog();
        }
    }

    private void showDatabaseTypeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DatabaseSettingActivity.this, R.style.MyDialogTheme));
        builder.setTitle(getString(R.string.type));
        builder.setItems(R.array.dataBaseTypes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                importDatabaseLocator.load(DatabaseSettingActivity.this, getStringObjectMap(which));
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getListView().setSelector(android.R.color.darker_gray);
        dialog.show();
    }

    @NonNull
    private Map<String, Object> getStringObjectMap(int which)
    {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put(CommonConstants.INDEX_KEY, which);
        objectMap.put(CommonConstants.TEXTVIEW_KEY, resultTextView);
        objectMap.put(CommonConstants.REVERT_DATABASE_BUTTON_KEY, defaultDatabaseButton);
        return objectMap;
    }

    private void setDefaultDatabaseButton()
    {
        defaultDatabaseButton = (Button) findViewById(R.id.default_database_button);
        defaultDatabaseButton.setVisibility(sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false) ? View.VISIBLE : View.GONE);
        defaultDatabaseButton.setOnClickListener(new DefaultDbOnClickListener());
    }

    private class DefaultDbOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            DialogConfiguration dialogConfiguration = new DialogConfiguration("",
                    getString(R.string.message_database_confirmation));
            CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(DatabaseSettingActivity.this, dialogConfiguration);
            customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try {
                        songDao.close();
                        songDao.copyDatabase("", true);
                        songDao.open();
                        sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false).apply();
                        defaultDatabaseButton.setVisibility(View.GONE);
                        updateResultTextview();
                        dialog.cancel();
                    } catch (IOException ex) {
                        Log.e(DatabaseSettingActivity.this.getClass().getSimpleName(), "Error occurred while coping database " + ex);
                    }
                }
            });
            customDialogBuilder.getBuilder().setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            customDialogBuilder.getBuilder().show();
        }
    }

    private void setResultTextView()
    {
        resultTextView = (TextView) findViewById(R.id.result_textview);
        resultTextView.setText(getCountQueryResult());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    doCopyFile(uri, getFileName(uri));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void doCopyFile(Uri uri, String fileName)
    {
        try {
            String fileExtension = FilenameUtils.getExtension(fileName);
            if ("sqlite".equalsIgnoreCase(fileExtension)) {
                showConfirmationDialog(uri, fileName);
            } else {
                copyFile(uri);
            }
        } finally {
            getDestinationFile().deleteOnExit();
        }
    }

    private void showConfirmationDialog(final Uri uri, String fileName)
    {
        String formattedMessage = String.format(getResources().getString(R.string.message_chooseDatabase_confirmation), fileName);
        DialogConfiguration dialogConfiguration = new DialogConfiguration(getString(R.string.confirmation),
                formattedMessage);
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(DatabaseSettingActivity.this, dialogConfiguration);
        customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                copyFile(uri);
                dialog.cancel();
            }
        });
        customDialogBuilder.getBuilder().setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        customDialogBuilder.getBuilder().show();
    }

    private void copyFile(Uri uri)
    {
        try {
            File destinationFile = getDestinationFile();
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputstream = new FileOutputStream(destinationFile);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            outputstream.write(data);
            inputStream.close();
            outputstream.close();
            Log.i(DatabaseSettingActivity.this.getClass().getSimpleName(), "Size of file " + FileUtils.sizeOf(destinationFile));
            validateDatabase(getDestinationFile().getAbsolutePath());
        } catch (IOException ex) {
            Log.i(DatabaseSettingActivity.class.getSimpleName(), "Error occurred while coping file" + ex);
        }
    }

    File getDestinationFile()
    {
        return new File(getCacheDir().getAbsolutePath(), CommonConstants.DATABASE_NAME);
    }

    private String getFileName(Uri uri)
    {
        File selectedFile = new File(uri.toString());
        String fileName = "";
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else {
            fileName = selectedFile.getName();
        }
        return fileName;
    }

    private void validateDatabase(String absolutePath)
    {
        try {
            resultTextView.setText("");
            songDao.close();
            songDao.copyDatabase(absolutePath, true);
            songDao.open();
            if (songDao.isValidDataBase()) {
                updateResultTextview();
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply();
                defaultDatabaseButton.setVisibility(sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY,
                        false) ? View.VISIBLE : View.GONE);
                FileUtils.deleteQuietly(PropertyUtils.getPropertyFile(DatabaseSettingActivity.this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME));
                Toast.makeText(this, R.string.import_database_successfull, Toast.LENGTH_SHORT).show();
            } else {
                showWarningDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(DatabaseSettingActivity.this.getClass().getSimpleName(), "Error occurred while coping external db" + e);
        }
    }

    private void updateResultTextview()
    {
        resultTextView.setText("");
        resultTextView.setText(getCountQueryResult());
    }

    private void showWarningDialog()
    {
        DialogConfiguration dialogConfiguration = new DialogConfiguration(getString(R.string.warning),
                getString(R.string.message_database_invalid));
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(DatabaseSettingActivity.this, dialogConfiguration);
        customDialogBuilder.getBuilder().setCancelable(false);
        customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    songDao.close();
                    songDao.copyDatabase("", true);
                    songDao.open();
                    dialog.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        customDialogBuilder.getBuilder().show();
    }

    public String getCountQueryResult()
    {
        String count = null;
        try {
            count = String.valueOf(songDao.count());
        } catch (Exception e) {
            count = "";
        }
        return String.format(getString(R.string.songs_count), count);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }

}
