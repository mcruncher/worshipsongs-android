package org.worshipsongs.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.SplashScreenActivity;
import org.worshipsongs.fragment.AlertDialogFragment;
import org.worshipsongs.parser.CommitMessageParser;
import org.worshipsongs.parser.ICommitMessageParser;
import org.worshipsongs.worship.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.worshipsongs.worship.R.string.url;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class HttpAsyncTask extends AsyncTask<String, String, String> implements AlertDialogFragment.DialogListener
{
    private static final String CLASS_NAME = HttpAsyncTask.class.getSimpleName();
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    private ProgressDialog progressDialog;
    private AppCompatActivity context;
    private SharedPreferences sharedPreferences;
    private ICommitMessageParser commitMessageParser = new CommitMessageParser();

    public HttpAsyncTask(AppCompatActivity context)
    {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog.setMessage(context.getString(R.string.check_update_message));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        String stringUrl = params[0];
        String result;
        String inputLine;
        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String jsonObject)
    {
        super.onPostExecute(jsonObject);
        final String shaKey = commitMessageParser.getShaKey(jsonObject);
        String existingShaKey = sharedPreferences.getString(CommonConstants.COMMIT_SHA_KEY, "");
        progressDialog.hide();
        Bundle bundle = new Bundle();
        bundle.putString(CommonConstants.COMMIT_SHA_KEY, shaKey);
        bundle.putString(CommonConstants.TITLE_KEY, context.getString(R.string.updates_title));
        if (existingShaKey.equalsIgnoreCase(shaKey)) {
            bundle.putString(CommonConstants.MESSAGE_KEY, context.getString(R.string.message_no_update));
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
            alertDialogFragment.setCancelable(false);
            alertDialogFragment.setVisibleNegativeButton(false);
            alertDialogFragment.setDialogListener(this);
            alertDialogFragment.show(context.getFragmentManager(), "NoUpdateFragment");
        } else {
            bundle.putString(CommonConstants.MESSAGE_KEY, context.getString(R.string.message_update_available));
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
            alertDialogFragment.setCancelable(false);
            alertDialogFragment.setDialogListener(this);
            alertDialogFragment.show(context.getFragmentManager(), "UpdateFragment");
        }
    }

    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        if ("NoUpdateFragment".equalsIgnoreCase(tag)) {
            context.finish();
        } else {
            new AsyncDownloadTask(context).execute(getRemoteUrl());
            sharedPreferences.edit().putString(CommonConstants.COMMIT_SHA_KEY, bundle.getString(CommonConstants.COMMIT_SHA_KEY)).apply();
        }
    }

    private String getRemoteUrl()
    {
        return sharedPreferences.getString(CommonConstants.REMOTE_URL, context.getString(R.string.remoteUrl));
    }

    @Override
    public void onClickNegativeButton()
    {
        context.finish();
    }
}
