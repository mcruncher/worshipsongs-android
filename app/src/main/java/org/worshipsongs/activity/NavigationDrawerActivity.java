package org.worshipsongs.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.WindowManager;

import org.worshipsongs.dialog.DefaultRemotePresentation;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.fragment.SongContentPortraitViewFragment;
import org.worshipsongs.service.DefaultPresentationScreenService;
import org.worshipsongs.worship.R;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer
{

    DefaultPresentationScreenService defaultPresentationScreenService;

    @Override
    public void init(Bundle bundle)
    {
        defaultPresentationScreenService = new DefaultPresentationScreenService(this);
        this.addSubheader("");
        this.addSection(newSection(getString(R.string.home), R.drawable.ic_library_books_white, new HomeTabFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_white, getSettingFragment()));
    }

    @NonNull
    private Fragment getSettingFragment()
    {
        return new Fragment()
        {
            @Override
            public void onStart()
            {
                super.onStart();
                Intent intent = new Intent(NavigationDrawerActivity.this, UserSettingActivity.class);
                startActivity(intent);
            }
        };
    }

    @Override
    public void onResume()
    {
        super.onResume();
        defaultPresentationScreenService.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        defaultPresentationScreenService.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        defaultPresentationScreenService.onResume();
    }
}
