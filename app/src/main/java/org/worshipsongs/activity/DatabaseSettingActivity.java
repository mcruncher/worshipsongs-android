package org.worshipsongs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.service.DatabaseService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.fragment.AlertDialogFragment;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.utils.PermissionUtils;

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

public class DatabaseSettingActivity extends AbstractAppCompactActivity implements AlertDialogFragment.DialogListener
{
    private IImportDatabaseLocator importDatabaseLocator = new ImportDatabaseLocator();
    private SongService songService = new SongService(WorshipSongApplication.getContext());
    private DatabaseService databaseService = new DatabaseService(WorshipSongApplication.getContext());
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
            if (PermissionUtils.isStoragePermissionGranted(DatabaseSettingActivity.this)) {
                showDatabaseTypeDialog();
            }
        }
    }

    private void showDatabaseTypeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyDialogTheme));
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
        defaultDatabaseButton.setVisibility(sharedPreferences.getBoolean(
                CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false) ? View.VISIBLE : View.GONE);
        defaultDatabaseButton.setOnClickListener(new DefaultDbOnClickListener());
    }

    private class DefaultDbOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.reset_default_title));
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_database_confirmation));
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
            alertDialogFragment.setDialogListener(DatabaseSettingActivity.this);
            alertDialogFragment.show(getFragmentManager(), "RevertDefaultDatabaseDialog");
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
            default:
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
            default:
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
        Bundle bundle = new Bundle();
        bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.confirmation));
        bundle.putString(CommonConstants.MESSAGE_KEY, String.format(getString(R.string.message_choose_local_db_confirmation), fileName));
        bundle.putString(CommonConstants.NAME_KEY, uri.toString());
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
        alertDialogFragment.setVisiblePositiveButton(true);
        alertDialogFragment.setVisibleNegativeButton(true);
        alertDialogFragment.setDialogListener(this);
        alertDialogFragment.show(getFragmentManager(), "DatabaseImportConfirmation");
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
            databaseService.close();
            databaseService.copyDatabase(absolutePath, true);
            databaseService.open();
            if (songService.isValidDataBase()) {
                updateResultTextview();
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply();
                defaultDatabaseButton.setVisibility(sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY,
                        false) ? View.VISIBLE : View.GONE);
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
        Bundle bundle = new Bundle();
        bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning));
        bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_database_invalid));
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
        alertDialogFragment.setDialogListener(this);
        alertDialogFragment.setVisibleNegativeButton(false);
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getFragmentManager(), "InvalidLocalDbWaringDialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case CommonConstants.STORAGE_PERMISSION_REQUEST_CODE:
                onRequestPermissionsResult(grantResults);
                return;
            default:
                break;
        }
    }

    protected void onRequestPermissionsResult(@NonNull int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showDatabaseTypeDialog();
        } else {
            Log.i(this.getClass().getSimpleName(), "Permission denied");
        }
    }

    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        if ("DatabaseImportConfirmation".equalsIgnoreCase(tag)) {
            String uriString = bundle.getString(CommonConstants.NAME_KEY);
            Uri uri = Uri.parse(uriString);
            copyFile(uri);
        } else if ("InvalidLocalDbWaringDialog".equalsIgnoreCase(tag)) {
            try {
                databaseService.close();
                databaseService.copyDatabase("", true);
                databaseService.open();
                updateResultTextview();
            } catch (IOException e) {
                Log.e(DatabaseSettingActivity.class.getSimpleName(), "Error", e);
            }
        } else if ("RevertDefaultDatabaseDialog".equalsIgnoreCase(tag)) {
            try {
                databaseService.close();
                databaseService.copyDatabase("", true);
                databaseService.open();
                updateResultTextview();
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false).apply();
                defaultDatabaseButton.setVisibility(View.GONE);
            } catch (IOException ex) {
                Log.e(DatabaseSettingActivity.this.getClass().getSimpleName(), "Error occurred while coping database " + ex);
            }
        }
    }

    @Override
    public void onClickNegativeButton()
    {
        //Do nothing
    }

    public String getCountQueryResult()
    {
        String count = null;
        try {
            count = String.valueOf(songService.count());
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
