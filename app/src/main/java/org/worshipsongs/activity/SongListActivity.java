package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class SongListActivity extends Activity {
    private ListView songListView;
    private VerseParser verseparser;
    private SongDao songDao;
    private List<Verse> verseList;
    private Song song;
    private Context context = this;
    private File serviceFile = null;
    List<String> songName;
    ListAdapter listAdapter;
    private List<String> serviceList = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter;
    AlertDialog alertDialog;
    ServiceListAdapter serviceListAdapter;
    public String popUpContents[];
    public PopupWindow popupWindow;
    private String selectedSong;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        songName = intent.getStringArrayListExtra("songNames");
        songListView = (ListView) findViewById(R.id.song_list_view);
        songDao = new SongDao(this);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        verseparser = new VerseParser();
        listAdapter = new ListAdapter(context);
        songListView.setAdapter(listAdapter);

        List<String> popUpList = new ArrayList<String>();
        popUpList.add("Add to service");
        popUpContents = new String[popUpList.size()];
        popUpList.toArray(popUpContents);
        popupWindow = popupWindow();
    }

    public PopupWindow popupWindow() {
        PopupWindow popupWindow1 = new PopupWindow(this);
        ListView listView = new ListView(this);
        listView.setAdapter(popUpAdapter(popUpContents));
        listView.setOnItemClickListener(new DropdownOnItemClickListener());
        popupWindow1.setFocusable(true);
        popupWindow1.setWidth(150);
        popupWindow1.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow1.setContentView(listView);
        return popupWindow1;
    }

    private ArrayAdapter<String> popUpAdapter(String popUpArray[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, popUpArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(SongListActivity.this);
                listItem.setText(text);
                listItem.setTag(position);
                listItem.setTextSize(18);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);
                return listItem;
            }
        };
        return adapter;
    }

    class DropdownOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
            popupWindow.dismiss();
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.service_name_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            final TextView title = (TextView) promptsView.findViewById(R.id.songTitle);
            final ListView serviceListView = (ListView) promptsView.findViewById(R.id.service_list);
            title.setText("Add to service");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            serviceList.clear();
            serviceList.add("New service...");
            serviceList = readServiceName();
            dataAdapter = new ArrayAdapter<String>(context, R.layout.service_alertdialog_content, serviceList);
            serviceListAdapter = new ServiceListAdapter(context);
            serviceListView.setAdapter(serviceListAdapter);
            serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    String service = dataAdapter.getItem(position);
                    System.out.println("Selected Song for Service:" + service);
                    if (position == 0) {
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.add_service_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setView(promptsView);
                        final TextView textViewServiceName = (TextView) promptsView.findViewById(R.id.textViewServiceName);
                        textViewServiceName.setTypeface(Typeface.DEFAULT_BOLD);
                        final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
                        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String service_name;
                                if (serviceName.getText().toString().equals(""))
                                    Toast.makeText(context, "Enter Service Name...!", Toast.LENGTH_LONG).show();
                                else {
                                    service_name = serviceName.getText().toString();
                                    saveIntoFile(service_name, selectedSong.toString());
                                    Toast.makeText(context, "Song added to service...!", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        saveIntoFile(service, selectedSong.toString());
                        Toast.makeText(context, "Song added to service...!", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                }
            });

            alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class ListAdapter extends BaseAdapter implements Filterable
    {
        SongsFilter songsFilter;
        LayoutInflater inflater;
        public ListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = inflater.inflate(R.layout.songs_listview_content, null);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(songName.get(position).toString().trim());
            final int temp = position;
            (convertView.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    String selectedValue = songName.get(temp).toString();
                    song = songDao.getSongByTitle(selectedValue);
                    String lyrics = song.getLyrics();
                    verseList = getVerse(lyrics);
                    List<String> verseName = new ArrayList<String>();
                    List<String> verseContent = new ArrayList<String>();
                    Map<String,String> verseDataMap = new HashMap<String, String>();
                    for (Verse verses : verseList) {
                        verseName.add(verses.getType() + verses.getLabel());
                        verseContent.add(verses.getContent());
                        verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
                    }
                    List<String> verseListDataContent = new ArrayList<String>();
                    List<String> verseListData = new ArrayList<String>();
                    String verseOrder = song.getVerseOrder();
                    if(StringUtils.isNotBlank(verseOrder))
                        verseListData = getVerseByVerseOrder(verseOrder);
                    Intent intent = new Intent(SongListActivity.this, SongsColumnViewActivity.class);
                    intent.putExtra("serviceName", selectedValue);
                    if(verseListData.size() > 0){
                        intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
                        for(int i=0; i<verseListData.size();i++){
                            verseListDataContent.add(verseDataMap.get(verseListData.get(i)));
                        }
                        intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
                        Log.d(this.getClass().getName(), "Verse List data content :" + verseListDataContent);
                    }
                    else{
                        intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                        intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                    }
                    startActivity(intent);
                }
            });

            (convertView.findViewById(R.id.serviceIcon)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(15);
                    selectedSong = songName.get(temp).toString();
                    popupWindow.showAsDropDown(arg0, -5, 0);
                }
            });
            return convertView;
        }

        public int getCount()
        {
            return songName.size();
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public Filter getFilter()
        {
            if (songsFilter == null)
                songsFilter = new SongsFilter();
            return songsFilter;
        }

        private class SongsFilter extends Filter
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = songName;
                    results.count = songName.size();
                } else {
                    ArrayList<String> filteredSongs = new ArrayList<String>();
                    for (int i = 0; i < songName.size(); i++) {
                        if (songName.get(i).toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filteredSongs.add(songName.get(i));
                        }
                    }
                    results.values = filteredSongs;
                    results.count = filteredSongs.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                songName = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    private List<Verse> getVerse(String lyrics) {
        return verseparser.parseVerseDom(this, lyrics);
    }

    private List<String> getVerseByVerseOrder(String verseOrder) {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i].toLowerCase());
        }
        Log.d("Verses list: ", verses.toString());
        return verses;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                listAdapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                listAdapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }

    public List readServiceName()
    {
        Properties property = new Properties();
        InputStream inputStream = null;
        int i = 0;
        try {
            serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            inputStream = new FileInputStream(serviceFile);
            property.load(inputStream);

            Enumeration<?> e = property.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                //String value = prop.getProperty(key);
                serviceList.add(key);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return serviceList;
    }

    private void saveIntoFile(String serviceName, String song)
    {
        try {
            serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            if (!serviceFile.exists()) {
                FileUtils.touch(serviceFile);
            }
            String existingProperty = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValue = "";
            if (StringUtils.isNotBlank(existingProperty)) {
                if (existingProperty.contains(song)) {
                    propertyValue = existingProperty;
                } else {
                    propertyValue = existingProperty + ";" + song;
                }
            } else {
                propertyValue = song;
            }
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }

    private class ServiceListAdapter extends BaseAdapter
    {
        LayoutInflater inflater;
        public ServiceListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = inflater.inflate(R.layout.service_alertdialog_content, null);
            TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);
            ImageView serviceIcon = (ImageView) convertView.findViewById(R.id.serviceIcon);
            if(position == 0)
                serviceIcon.setImageResource(R.drawable.file);
            serviceName.setText(serviceList.get(position).toString().trim());
            return  convertView;
        }

        @Override
        public int getCount() {
            return serviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}