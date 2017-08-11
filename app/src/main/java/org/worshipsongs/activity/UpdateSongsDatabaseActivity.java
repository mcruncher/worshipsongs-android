package org.worshipsongs.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.AlertDialogFragment;
import org.worshipsongs.task.HttpAsyncTask;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.R;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class UpdateSongsDatabaseActivity extends AppCompatActivity implements AlertDialogFragment.DialogListener
{
    private static final String DB_API_URL = "https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/refs/heads/master";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.update_song_database_layout);
        updateSongDatabase();
    }

    private void hideStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void updateSongDatabase()
    {
        if (CommonUtils.isWifiOrMobileDataConnectionExists(this)) {
            new HttpAsyncTask(this).execute(DB_API_URL);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning));
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_warning_internet_connection));
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
            alertDialogFragment.setVisibleNegativeButton(false);
            alertDialogFragment.setDialogListener(this);
            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        finish();
    }

    @Override
    public void onClickNegativeButton()
    {
        //Do nothing when click negative button
    }
}
