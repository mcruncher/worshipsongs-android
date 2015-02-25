package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.page.component.fragment.ServiceListFragment;
import org.worshipsongs.page.component.fragment.SongsListFragment;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;
import org.worshipsongs.page.component.fragment.VerseContentView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 10/8/2014.
 */
public class SongsViewActivity extends FragmentActivity
{
    private ViewPager viewPager;
    private ActionBar actionBar;
    List<String> verseName;
    List<String> verseContent;
    private boolean addPadding = true;
    final Context context = this;
    ServiceListFragment serviceListFragment = new ServiceListFragment();
    private File serviceFile = null;
    String serviceName;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_page_listener);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    private void init()
    {
        viewPager = (ViewPager) findViewById(R.id.pager);
        setContentView(viewPager);
        //  Init and set ActionBar Properties.
        actionBar = getActionBar();
        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(true);
        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(true);
        // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //  Initialise Adapter for the view pager.
        TabAdapter adapter = new TabAdapter();

        actionBar.addTab(actionBar.newTab().setText("Songs").setTabListener(adapter));
        actionBar.addTab(actionBar.newTab().setText("Service").setTabListener(adapter));

        viewPager.setBackgroundResource(R.drawable.rounded_corner);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(adapter);
    }

    private class TabAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {

        private ArrayList<Bundle> bundleArrayList;

        public TabAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            //  Change the tab selection upon page selection.
            actionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction arg1) {
            //  On Tab selection change the View Pager's Current item position.
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

        }

        @Override
        public Fragment getItem(int pos)
        {
            if (pos == 0)
                return new SongsListFragment();
            else if(pos == 1)
                return new ServiceListFragment();
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            intent = new Intent(SongsViewActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_about)
        {
            intent = new Intent(SongsViewActivity.this, AboutWebViewActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}