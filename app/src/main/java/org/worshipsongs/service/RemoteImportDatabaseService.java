package org.worshipsongs.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.activity.SplashScreenActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.CustomDialogBuilder;
import org.worshipsongs.domain.DialogConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class RemoteImportDatabaseService implements ImportDatabaseService
{

    private Map<String, Object> objects;
    private SongDao songDao;
    private AppCompatActivity appCompatActivity;
    private SharedPreferences sharedPreferences;
    private String remoteUrl;

    @Override
    public void loadDb(AppCompatActivity appCompatActivity, Map<String, Object> objects)
    {
        this.appCompatActivity = appCompatActivity;
        songDao = new SongDao(appCompatActivity);
        this.objects = objects;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        setDefaultRemoteUrl();
        if (isWifiOrMobileDataConnectionExists()) {
            showRemoteUrlConfigurationDialog();
        } else {
            showNetWorkWarningDialog();
        }
    }

    @Override
    public String getName()
    {
        return RemoteImportDatabaseService.class.getSimpleName();
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    private void setDefaultRemoteUrl()
    {
        if (!sharedPreferences.getAll().containsKey(CommonConstants.REMOTE_URL)) {
            sharedPreferences.edit().putString(CommonConstants.REMOTE_URL, appCompatActivity.getString(R.string.remoteUrl)).apply();
        }
    }

    private boolean isWifiOrMobileDataConnectionExists()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) appCompatActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) || (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showRemoteUrlConfigurationDialog()
    {
        DialogConfiguration dialogConfiguration = new DialogConfiguration(appCompatActivity.getString(R.string.url), "");
        dialogConfiguration.setEditTextVisibility(true);
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(appCompatActivity, dialogConfiguration);
        final EditText editText = customDialogBuilder.getEditText();
        editText.setText(sharedPreferences.getString(CommonConstants.REMOTE_URL, appCompatActivity.getString(R.string.remoteUrl)));
        AlertDialog.Builder builder = customDialogBuilder.getBuilder();
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String url = editText.getText().toString();
                new AsyncDownloadTask().execute(url);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showNetWorkWarningDialog()
    {
        DialogConfiguration dialogConfiguration = new DialogConfiguration(appCompatActivity.getString(R.string.warning),
                appCompatActivity.getString(R.string.message_network_warning));
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(appCompatActivity, dialogConfiguration);
        customDialogBuilder.getBuilder().setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        customDialogBuilder.getBuilder().show();
    }

    private class AsyncDownloadTask extends AsyncTask<String, Void, Boolean>
    {
        private File destinationFile = null;
        private ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(appCompatActivity, R.style.MyDialogTheme));
        private TextView resultTextView = (TextView) objects.get(CommonConstants.TEXTVIEW_KEY);

        @Override
        protected void onPreExecute()
        {
            progressDialog.setTitle(appCompatActivity.getString(R.string.downloading_title));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            try {
                int count;
                destinationFile = new File(appCompatActivity.getCacheDir().getAbsolutePath(), CommonConstants.DATABASE_NAME);
                remoteUrl = strings[0];
                URL url = new URL(remoteUrl);
                URLConnection conection = url.openConnection();
                conection.setReadTimeout(60000);
                conection.setConnectTimeout(60000);
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 10 * 1024);
                // Output stream to write file in SD card
                OutputStream output = new FileOutputStream(destinationFile);
                byte data[] = new byte[1024];
                long total = 0;
                progressDialog.setProgress(0);
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    progressDialog.setProgress((int) ((total * 100) / lenghtOfFile));
                }
                output.flush();
                output.close();
                input.close();
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
            resultTextView.setText("");
            if (successfull) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Remote database copied successfully.");
                validateDatabase(destinationFile.getAbsolutePath(), resultTextView);
            } else {
                showWarningDialog(resultTextView);
            }
            progressDialog.cancel();
        }
    }

    private void validateDatabase(String absolutePath, TextView resultTextView)
    {
        try {
            songDao.close();
            songDao.copyDatabase(absolutePath, true);
            songDao.open();
            if (songDao.isValidDataBase()) {
                Button revertDatabaseButton = (Button) objects.get(CommonConstants.REVERT_DATABASE_BUTTON_KEY);
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply();
                revertDatabaseButton.setVisibility(sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false) ? View.VISIBLE : View.GONE);
                resultTextView.setText(getCountQueryResult());
                sharedPreferences.edit().putString(CommonConstants.REMOTE_URL, remoteUrl).apply();
                sharedPreferences.edit().remove(CommonConstants.COMMIT_SHA_KEY).apply();
                Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.import_database_successfull), Toast.LENGTH_SHORT).show();
            } else {
                showWarningDialog(resultTextView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showWarningDialog(final TextView resultTextView)
    {
        DialogConfiguration dialogConfiguration = new DialogConfiguration(appCompatActivity.getString(R.string.warning),
                appCompatActivity.getString(R.string.message_invalid_url));
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(appCompatActivity, dialogConfiguration);
        customDialogBuilder.getBuilder().setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    songDao.close();
                    songDao.copyDatabase("", true);
                    songDao.open();
                    resultTextView.setText(getCountQueryResult());
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
        String count = String.valueOf(songDao.count());
        return String.format(appCompatActivity.getString(R.string.songs_count), count);
    }

}
