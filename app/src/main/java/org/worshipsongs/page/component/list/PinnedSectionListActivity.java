package org.worshipsongs.page.component.list;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomTabSettings;
import org.worshipsongs.activity.SongsListActivity;
import org.worshipsongs.activity.SongsViewActivity;
import org.worshipsongs.activity.UserSettingActivity;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class PinnedSectionListActivity extends ListActivity implements OnClickListener
{
    private List<String> verseName;
    private List<String> verseContent;
    private ActionBar actionBar;
    private TextView textView;
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private Context context = WorshipSongApplication.getContext();

    class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter
    {
        private final int[] COLORS = new int[]{
                R.color.liteGray};
        public SimpleAdapter(Context context, int resource, int textViewResourceId)
        {
            super(context, resource, textViewResourceId);
            generateDataset();
        }

        public void generateDataset()
        {
            final int sectionsNumber = verseName.size();
            int sectionPosition = 0, listPosition = 0;
            Item section = new Item(Item.SECTION, "Verses");
            section.sectionPosition = sectionPosition;
            add(section);
            for (char i = 0; i < sectionsNumber; i++) {
                Item item = new Item(Item.ITEM, verseContent.get(i));
                section.listPosition = listPosition++;
                add(item);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            textView = (TextView) super.getView(position, convertView, parent);
            textView.setTextColor(Color.DKGRAY);
            textView.setTag("" + position);
            Item item = getItem(position);
            if (item.type == Item.SECTION) {
                textView.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if (item.type != Item.SECTION) {
                preferenceSettingService = new UserPreferenceSettingService();
                String text = textView.getText().toString();
                Log.d(this.getClass().getName(), "Text View" + textView.getText());
                customTagColorService = new CustomTagColorService();
                textView.setText("");
                customTagColorService.setCustomTagTextView(context, text.toString(), textView);
                textView.setTypeface(preferenceSettingService.getTypeFace(), preferenceSettingService.getFontStyle());
                textView.setTextSize(preferenceSettingService.getFontSize());
                textView.setTextColor(preferenceSettingService.getColor());
                textView.setVerticalScrollBarEnabled(true);
            }
            return textView;
        }

        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getItemViewType(int position)
        {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType)
        {
            return viewType == Item.SECTION;
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListAdapter(new SimpleAdapter(this, R.layout.pinned_list_item, R.id.text1));
    }

    static class Item
    {
        public static final int ITEM = 0;
        public static final int SECTION = 1;
        public final int type;
        public final String text;
        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text)
        {
            this.type = type;
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    private boolean isSectionView = true;
    private boolean isTabView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinned_section_actvity_main);
        actionBar = getActionBar();
        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(true);
        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            isSectionView = savedInstanceState.getBoolean("isSectionView");
            isTabView = savedInstanceState.getBoolean("isTabView");
        }
        verseName = new ArrayList<String>();
        verseContent = new ArrayList<String>();
        verseName = intent.getStringArrayListExtra("verseName");
        verseContent = intent.getStringArrayListExtra("verseContent");
        initializeAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSectionView", isSectionView);
        outState.putBoolean("isTabView", isTabView);
    }

    @Override
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
        menu.findItem(R.id.sectionView).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                intent = new Intent(this, SongsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.sectionView:
                break;
            case R.id.tabbedView:
                isTabView = !isTabView;
                item.setChecked(isTabView);
                intent = new Intent(this, SongsViewActivity.class);
                intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent(this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.custom_tab_settings:
                intent = new Intent(this, CustomTabSettings.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private void initializeAdapter()
    {
        setListAdapter(new SimpleAdapter(this, R.layout.pinned_list_item, R.id.text1));
    }

    @Override
    public void onClick(View v)
    {
    }
}