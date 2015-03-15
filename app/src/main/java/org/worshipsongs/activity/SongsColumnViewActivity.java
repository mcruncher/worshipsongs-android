package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import org.worshipsongs.adapter.CustomListViewAdapter;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongsColumnViewActivity extends ListActivity
{
    private UserPreferenceSettingService userPreferenceSettingService;
    private List<String> verseName;
    private List<String> verseContent;
    private TextView textView;
    private ActionBar actionBar;
    private CustomListViewAdapter customListViewAdapter;
    private boolean isSectionView = true;
    private boolean isTabView = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userPreferenceSettingService = new UserPreferenceSettingService();
        if(userPreferenceSettingService.getKeepAwakeStatus()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (savedInstanceState != null) {
            isSectionView = savedInstanceState.getBoolean("isSectionView");
            isTabView = savedInstanceState.getBoolean("isTabView");
        }

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        verseName = new ArrayList<String>();
        verseContent = new ArrayList<String>();
        verseName = intent.getStringArrayListExtra("verseName");
        verseContent = intent.getStringArrayListExtra("verseContent");
        Log.d(this.getClass().getName(),"Verse Name :"+ verseName);
        Log.d(this.getClass().getName(),"Verse Content :"+ verseName);

        textView = ((TextView) this.findViewById(R.id.text));
        initializeAdapter();
    }

    private void initializeAdapter()
    {
        customListViewAdapter = new CustomListViewAdapter(this);
        if (verseName != null) {
            for (int i = 0; i < verseName.size(); i++) {
                Log.d(this.getClass().getName(),"customListViewAdapter Verse Content :"+ verseContent.get(i));
                customListViewAdapter.addItem(verseContent.get(i));
            }
        }
        setListAdapter(customListViewAdapter);
    }

    public void onResume()
    {
        super.onResume();
        setListAdapter(customListViewAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSectionView", isSectionView);
        outState.putBoolean("isTabView", isTabView);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }
}