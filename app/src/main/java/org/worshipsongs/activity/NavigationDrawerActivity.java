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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.List;


public class NavigationDrawerActivity extends AbstractAppCompactActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int UPDATE_DB_REQUEST_CODE = 555;
    private static final String SENDER_MAIL = "appfeedback@mcruncher.com";
    private SharedPreferences sharedPreferences;
    private PresentationScreenService presentationScreenService;
    private FragmentRegistry fragmentRegistry = new FragmentRegistry();

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
        Menu menu = navigationView.getMenu();
        addTabsMenu(menu);
        addMenus(menu);
        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().getItem(0);
            onNavigationItemSelected(item);
            navigationView.setCheckedItem(item.getItemId());
        }
        ColorStateList colorStateList = getColorStateList();
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
    }

    private void setHeaderView(NavigationView navigationView)
    {
        View headerView = navigationView.getHeaderView(0);
        TextView headerSubTitleTextView = (TextView) headerView.findViewById(R.id.header_subtitle);
        headerSubTitleTextView.setText(getString(R.string.noOfSongsAvailable,
                sharedPreferences.getLong(CommonConstants.NO_OF_SONGS, 0)));
    }

    private void addTabsMenu(Menu menu)
    {
        List<String> titles = fragmentRegistry.getTitles(this);
        for (int i = 0; i < titles.size(); i++) {
            String titleKey = titles.get(i);
            String title = getString(getResources().getIdentifier(titleKey, "string",
                    getPackageName()));
            menu.add(0, i, i, title);
            MenuItem item = menu.getItem(i);
        }
    }

    private void addMenus(Menu menu)
    {
        menu.add(0, 5, 5, getString(R.string.update_songs));
        MenuItem updateSongMenuItem = menu.getItem(5);
        updateSongMenuItem.setIcon(getResources().getDrawable(android.R.drawable.stat_sys_download));

        menu.add(0, 6, 6, getString(R.string.settings));
        MenuItem settingMenuItem = menu.getItem(6);
        settingMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_settings_white));

        menu.add(0, 7, 7, getString(R.string.rate_this_app));
        MenuItem rateMenuItem = menu.getItem(7);
        rateMenuItem.setIcon(getResources().getDrawable(android.R.drawable.star_off));

        menu.add(0, 8, 8, getString(R.string.share));
        MenuItem shareMenuItem = menu.getItem(8);
        shareMenuItem.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_share));

        menu.add(0, 9, 9, getString(R.string.feedback));
        MenuItem feedBackMenuItem = menu.getItem(9);
        feedBackMenuItem.setIcon(getResources().getDrawable(android.R.drawable.sym_action_email));

        menu.add(0, 10, 10, getString(R.string.version, CommonUtils.getProjectVersion()));
        MenuItem versionMenuItem = menu.getItem(10);
        versionMenuItem.setEnabled(false);
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
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                setHomeView(item.getTitle().toString(), item.getItemId());
                break;
            case 5:
                setUpdateView();
                break;
            case 6:
                startActivity(new Intent(NavigationDrawerActivity.this,
                        UserSettingActivity.class));
                break;
            case 7:
                setRateUsView();
                break;
            case 8:
                setShareView();
                break;
            case 9:
                setEmail();
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHomeView(String title, int menuItemId)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment existingHomeTabFragment = (HomeFragment) fragmentManager
                .findFragmentByTag(HomeFragment.class.getSimpleName());
        if (isNewTabSelected(existingHomeTabFragment, menuItemId)) {
            Fragment fragment = HomeFragment.newInstance();
            fragment.setArguments(getBundle(title, menuItemId));
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, HomeFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.i(NavigationDrawerActivity.class.getSimpleName(), "Existing tab selected");
        }
    }

    @NonNull
    private Bundle getBundle(String title, int menuItemId)
    {
        Bundle bundle = getIntent().getExtras() == null ? new Bundle() : getIntent().getExtras();
        bundle.putString(CommonConstants.TAB_TITLE_KEY, title);
        bundle.putInt(CommonConstants.TAB_SELECTED_ITEM_ID, menuItemId);
        return bundle;
    }

    boolean isNewTabSelected(HomeFragment homeFragment, int selectedMenuItemId)
    {
        if (homeFragment != null) {
            ViewPager viewPager = homeFragment.getView().findViewById(R.id.pager);
            int existingCurrentItem = viewPager.getCurrentItem();
            return selectedMenuItemId != existingCurrentItem;
        }
        return true;
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
