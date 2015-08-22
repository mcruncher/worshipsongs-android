package org.worshipsongs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.fragment.SettingsFragment;
import org.worshipsongs.fragment.SettingsPreferenceFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer
{

    @Override
    public void init(Bundle bundle)
    {
        //this.setContentView();
        this.addSection(newSection("Home", new HomeTabFragment()));
        Fragment fragment = new Fragment()
        {
            @Override
            public void onStart()
            {
                super.onStart();
                Intent intent = new Intent(NavigationDrawerActivity.this, UserSettingActivity.class);
                startActivity(intent);
            }
        };

        this.addSection(newSection("Settings", fragment));
       // this.addSection(newSection("Settings", new SettingsFragment()));
    }


}
