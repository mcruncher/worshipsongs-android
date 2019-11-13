package org.worshipsongs.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.fragment.HomeFragment;
import org.worshipsongs.registry.FragmentRegistry;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.ThemeUtils;


public class NavigationDrawerActivity extends AbstractAppCompactActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int UPDATE_DB_REQUEST_CODE = 555;
    private static final String SENDER_MAIL = "appfeedback@mcruncher.com";
    private SharedPreferences sharedPreferences;
    private PresentationScreenService presentationScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThemeUtils.setNoActionBarTheme(this);
        setContentView(R.layout.activity_main);
        presentationScreenService = new PresentationScreenService(this);
        setSongCount();
        setDrawerLayout();
        setNavigationView(savedInstanceState);
    }

    private void setSongCount()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getAll().containsKey(CommonConstants.NO_OF_SONGS)) {
            SongService songService = new SongService(this);
            long count = songService.count();
            sharedPreferences.edit().putLong(CommonConstants.NO_OF_SONGS, count).apply();
        }
    }

    private void setDrawerLayout()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigationView(Bundle savedInstanceState)
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setHeaderView(navigationView);
        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().getItem(0);
            onNavigationItemSelected(item);
            navigationView.setCheckedItem(R.id.home);
        }
        ColorStateList colorStateList = getColorStateList();
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
        TextView versionTextView = (TextView) navigationView.findViewById(R.id.version);
        versionTextView.setText(getString(R.string.version, CommonUtils.getProjectVersion()));
    }

    private void setHeaderView(NavigationView navigationView)
    {
        View headerView = navigationView.getHeaderView(0);
        TextView headerSubTitleTextView = (TextView) headerView.findViewById(R.id.header_subtitle);
        headerSubTitleTextView.setText(getString(R.string.noOfSongsAvailable,
                sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0)));
    }


    @NonNull
    private ColorStateList getColorStateList()
    {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int[] color = new int[]{
                typedValue.data,
                typedValue.data,
                typedValue.data,
                typedValue.data
        };
        return new ColorStateList(state, color);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.home:
                setHomeView();
                break;
            case R.id.updateSongs:
                setUpdateView();
                break;
            case R.id.settings:
                startActivity(new Intent(NavigationDrawerActivity.this,
                        UserSettingActivity.class));
                break;
            case R.id.rateUs:
                setRateUsView();
                break;
            case R.id.share:
                setShareView();
                break;
            case R.id.feedback:
                setEmail();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHomeView()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment existingHomeTabFragment = (HomeFragment) fragmentManager
                .findFragmentByTag(HomeFragment.class.getSimpleName());
        if (existingHomeTabFragment == null) {
            Fragment fragment = HomeFragment.newInstance();
            fragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, HomeFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    private void setUpdateView()
    {
        Intent updateSongs = new Intent(NavigationDrawerActivity.this,
                UpdateSongsDatabaseActivity.class);
        startActivityForResult(updateSongs, UPDATE_DB_REQUEST_CODE);
    }

    private void setRateUsView()
    {
        Uri uri = Uri.parse("market://details?id=" + NavigationDrawerActivity.
                this.getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(getFlags());
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            NavigationDrawerActivity.this.getApplicationContext().getPackageName())));
        }
    }

    private void setShareView()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.app_description) + getString(R.string.app_download_info));
        shareIntent.setType("text/plain");
        Intent intent = Intent.createChooser(shareIntent, getString(R.string.share) + " "
                + getString(R.string.app_name) + " in");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareIntent);
    }

    int getFlags()
    {
        return (Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    private void setEmail()
    {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setData(Uri.parse("mailto:" + SENDER_MAIL));
        mailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject(getApplicationContext()));
        startActivity(Intent.createChooser(mailIntent, ""));
    }

    String getEmailSubject(Context context)
    {
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName()
                    , 0).versionName;
            return String.format(context.getString(R.string.feedback_subject), versionName);
        } catch (PackageManager.NameNotFoundException e) {
            return getString(R.string.feedback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case UPDATE_DB_REQUEST_CODE:
                long noOfSongs = sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0);
                sharedPreferences.edit().putLong(CommonConstants.NO_OF_SONGS, noOfSongs).apply();
                TextView headerSubTitleTextView = (TextView) findViewById(R.id.header_subtitle);
                headerSubTitleTextView.setText(getString(R.string.noOfSongsAvailable, noOfSongs));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onResume();
    }
}
