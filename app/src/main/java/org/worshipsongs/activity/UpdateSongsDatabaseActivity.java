package org.worshipsongs.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.AlertDialogFragment;
import org.worshipsongs.task.HttpAsyncTask;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class UpdateSongsDatabaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        setContentView(R.layout.update_song_database_layout);
        if (CommonUtils.isWifiOrMobileDataConnectionExists(this)) {
            new HttpAsyncTask(this).execute("https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/refs/heads/master");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TITLE_KEY, "Warning");
            bundle.putString(CommonConstants.MESSAGE_KEY, "Internet connection is needed to update the song database");
            AlertDialogFragment.newInstance(bundle).show(getFragmentManager(), AlertDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
