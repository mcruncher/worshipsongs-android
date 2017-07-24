package org.worshipsongs.task;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.worship.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class AsyncDownloadTask extends AsyncTask<String, Integer, Boolean>
{
    private ProgressBar progressBar;
    private AppCompatActivity context;
    private File destinationFile = null;
    private SongDao songDao;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView progressTextView;

    public AsyncDownloadTask(AppCompatActivity context)
    {
        this.context = context;
        songDao = new SongDao(context);
        setDialogBuilder(context);
    }

    private void setDialogBuilder(final AppCompatActivity context)
    {
        builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyDialogTheme));
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.dialog_custom_title, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setText(R.string.download_song_database);
        TextView textView = (TextView) view.findViewById(R.id.subtitle);
        textView.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        TextView progressInitialTextSize = (TextView) view.findViewById(R.id.initial_text_view);
        progressInitialTextSize.setVisibility(View.VISIBLE);

        progressTextView = (TextView) view.findViewById(R.id.progress_text_view);
        progressTextView.setVisibility(View.VISIBLE);
        builder.setView(view);
    }

    @Override
    protected void onPreExecute()
    {
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        try {
            int count;
            destinationFile = new File(context.getCacheDir().getAbsolutePath(), CommonConstants.DATABASE_NAME);
            String remoteUrl = strings[0];
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
            publishProgress(0);
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
                publishProgress((int) ((total * 100) / lenghtOfFile));

            }
            output.flush();
            output.close();
            input.close();
            return true;
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error", ex);
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
        progressTextView.setText(values[0] + "/100");
    }

    @Override
    protected void onPostExecute(Boolean successfull)
    {
        try {
            alertDialog.cancel();
            if (successfull) {
                Log.i(AsyncDownloadTask.class.getSimpleName(), "Remote database copied successfully.");
                songDao.close();
                songDao.copyDatabase(destinationFile.getAbsolutePath(), true);
                songDao.open();
                FileUtils.deleteQuietly(destinationFile);
                context.finish();
            }
        } catch (Exception ex) {
            Log.e(AsyncDownloadTask.class.getSimpleName(), "Error");
        }
    }


}
