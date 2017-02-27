package org.worshipsongs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.worshipsongs.fragment.DatabaseFragment;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.worship.R;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer
{

   private PresentationScreenService presentationScreenService;

    @Override
    public void init(Bundle bundle)
    {
        presentationScreenService = new PresentationScreenService(this);
        this.addSubheader("");
        this.addSection(newSection(getString(R.string.home), R.drawable.ic_library_books_white, new HomeTabFragment()));
        this.addSection(newSection("Database", android.R.drawable.ic_menu_upload, DatabaseFragment.newInstance()));
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
        presentationScreenService.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        presentationScreenService.onResume();
    }
}
