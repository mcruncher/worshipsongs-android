package org.worshipsongs.service;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SplashScreenActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.worship.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class RemoteImportDatabaseService implements ImportDatabaseService
{
    private SongDao songDao;
    private ProgressBar progressBar;
    private Context context;

    @Override
    public void loadDb(Context context, Fragment fragment)
    {
        this.context = context;
        songDao = new SongDao(context);
        if (isWifiOrMobileDataConnectionExists()) {
            new AsyncDownloadTask().execute();
        } else {
            showNetWorkWarningDialog();
        }
    }

    @Override
    public void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
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

    private boolean isWifiOrMobileDataConnectionExists()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void showNetWorkWarningDialog()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
        TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
        deleteMsg.setText(R.string.message_network_warning);
        AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(context, R.style.MyDialogTheme)));
        builder.setView(promptsView);
        builder.setTitle(R.string.warning);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private class AsyncDownloadTask extends AsyncTask<String, Void, Boolean>
    {
        File destinationFile = null;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            try {
                int count;
                destinationFile = new File(context.getCacheDir().getAbsolutePath(), CommonConstants.DATABASE_NAME);
                String remoteUrl = "https://raw.githubusercontent.com/crunchersaspire/worshipsongs-db-dev/master/songs.sqlite";
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
                while ((count = input.read(data)) != -1) {
                    // Write data to file
                    output.write(data, 0, count);
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
            if (successfull) {
                Log.i(SplashScreenActivity.class.getSimpleName(), "Remote database copied successfully.");
                validateDatabase(destinationFile.getAbsolutePath());
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void validateDatabase(String absolutePath)
    {
        try {
            songDao.copyDatabase(absolutePath, true);
            songDao.open();
            if (songDao.isValidDataBase()) {
                Toast.makeText(context, R.string.message_database_successfull, Toast.LENGTH_SHORT).show();
            } else {
                showWarningDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showWarningDialog()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
        TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
        deleteMsg.setText(R.string.message_database_invalid);
        AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(context, R.style.MyDialogTheme)));
        builder.setView(promptsView);
        builder.setTitle(R.string.warning);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    songDao.copyDatabase("", true);
                    songDao.open();
                    dialog.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

}
