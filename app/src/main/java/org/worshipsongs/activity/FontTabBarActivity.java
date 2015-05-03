package org.worshipsongs.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import org.worshipsongs.worship.R;

/**
 * author:madasamy
 * version:1.2.0
 */
public class FontTabBarActivity extends TabActivity implements TabHost.OnTabChangeListener
{
    /**
     * Called when the activity is first created.
     */
    private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_tab_bar);

        // Get TabHost Refference
        tabHost = getTabHost();

        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        /************* TAB1 ************/
        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FontSizeTabActivity.class);
        spec = tabHost.newTabSpec("First").setIndicator("")
                .setContent(intent);

        //Add intent to tab
        tabHost.addTab(spec);

        /************* TAB2 ************/
        intent = new Intent().setClass(this, FontStyleActivity.class);
        spec = tabHost.newTabSpec("Second").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB3 ************/
//        intent = new Intent().setClass(this, Tab3.class);
//        spec = tabHost.newTabSpec("Third").setIndicator("")
//                .setContent(intent);
//        tabHost.addTab(spec);

        // Set drawable images to tab
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab2);
        //  tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.tab3);

        // Set Tab1 as Default tab and change image
        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab2);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab1);


    }

    @Override
    public void onTabChanged(String tabId)
    {

        /************ Called when tab changed *************/

        //********* Check current selected tab and change according images *******/

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            if (i == 0)
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab1);
            else if (i == 1)
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab2);
//            else if(i==2)
//                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab3);
        }


        Log.i("tabs", "CurrentTab: " + tabHost.getCurrentTab());

        if (tabHost.getCurrentTab() == 0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.tab1_over);
        else if (tabHost.getCurrentTab() == 1)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.tab2_over);
//        else if(tabHost.getCurrentTab()==2)
//            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.tab3_over);

    }
}
