package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.worshipsongs.worship.R;
import org.worshipsongs.page.component.fragment.VerseContentView;

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

	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_page_listener);
        Intent intent = getIntent();
        verseName = new ArrayList<String>();
        verseContent = new ArrayList<String>();
        verseName  = intent.getStringArrayListExtra("verseName");
        verseContent = intent.getStringArrayListExtra("verseContent");
        Log.d(this.getClass().getName(), "Verse name Size:" + verseName.size());
        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        setContentView(viewPager);
        //  Init and set ActionBar Properties.
        actionBar = getActionBar();
        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(true);
        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //  Initialise Adapter for the view pager.
        TabAdapter adapter = new TabAdapter();
        ArrayList<Bundle> verseBundle = new ArrayList<Bundle>();
        for(int index = 0; index < verseName.size(); index++)
        {
            //  Prepare Bundle object for each tab.
            Bundle bundle = new Bundle();
            bundle.putString("verseData", verseContent.get(index));
            verseBundle.add(bundle);
            ActionBar.Tab tab = actionBar.newTab().setText(verseName.get(index))
                    .setTabListener(adapter);
            actionBar.addTab(tab);
        }
        adapter.setBundle(verseBundle);
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

        /**
         * ArrayList of bundle's to pass as Arguments to Fragment.
         * @param bundles  ArrayList of bundle's
         */
        void setBundle(ArrayList<Bundle> bundles) {
            bundleArrayList = bundles;
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
        public Fragment getItem(int pos) {
            return Fragment.instantiate(SongsViewActivity.this, VerseContentView.class.getName(), bundleArrayList.get(pos));
        }

        @Override
        public int getCount() {
            return bundleArrayList.size();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.tabbedView).setVisible(false);
        menu.findItem(R.id.sectionView).setVisible(true);
        menu.findItem(R.id.sectionView).setCheckable(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(R.id.tabbedView).setCheckable(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                intent = new Intent(this, SongsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent(SongsViewActivity.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.sectionView:
                intent = new Intent(this, SongsColumnViewActivity.class);
                intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                startActivity(intent);
                break;
            case R.id.custom_tab_settings:
                intent = new Intent(this, CustomTabSettings.class);
                startActivity(intent);
                break;

        }
        return true;
    }

}