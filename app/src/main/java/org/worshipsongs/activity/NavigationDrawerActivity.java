package org.worshipsongs.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.fragment.HomeFragment;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.service.SongService;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int UPDATE_DB_REQUEST_CODE = 555;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getAll().containsKey(CommonConstants.NO_OF_SONGS)) {
            SongService songService = new SongService(this);
            long count = songService.count();
            sharedPreferences.edit().putLong(CommonConstants.NO_OF_SONGS, count).apply();
        }
        Log.i(NavigationDrawerActivity.class.getSimpleName(), "No of songs"+ sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0));
        TextView headerSubTitleTextView = (TextView) findViewById(R.id.header_subtitle);
        headerSubTitleTextView.setText(getString(R.string.noOfSongsAvailable, sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0)));
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            HomeFragment existingHomeTabFragment = (HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
            if (existingHomeTabFragment == null) {
                fragment = HomeFragment.newInstance();
                fragment.setArguments(getIntent().getExtras());
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment, HomeFragment.class.getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (id == R.id.updateSongs) {
            Intent updateSongs = new Intent(NavigationDrawerActivity.this, UpdateSongsDatabaseActivity.class);
            startActivityForResult(updateSongs, UPDATE_DB_REQUEST_CODE);
        } else if (id == R.id.settings) {

        } else if (id == R.id.rateUs) {

        } else if (id == R.id.share) {

        } else if (id == R.id.feedback) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case UPDATE_DB_REQUEST_CODE:
                long noOfSongs = sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0);
//                if (this.getAccountList().size() > 0) {
//                    this.getAccountAtCurrentPosition(0).setSubTitle(getString(R.string.noOfSongsAvailable, noOfSongs));
//                    this.notifyAccountDataChanged();
                TextView headerSubTitleTextView = (TextView) findViewById(R.id.header_subtitle);
                headerSubTitleTextView.setText(getString(R.string.noOfSongsAvailable, noOfSongs));
//
                break;
            default:
                break;
        }
    }
}
