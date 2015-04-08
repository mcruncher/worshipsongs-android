package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Seenivasan on 3/21/2015.
 */
public class ServiceSongListActivity extends Activity {

    private ListView songListView;
    private VerseParser verseparser;
    private String songTitle;
    private SongDao songDao;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<String> adapter;
    private String[] dataArray;
    private Song song;
    String serviceName;
    private Context context = WorshipSongApplication.getContext();
    private File serviceFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        serviceName = intent.getStringExtra("serviceName");
        System.out.println("Selected Service:"+serviceName);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(serviceName);


        songListView = (ListView) findViewById(R.id.song_list_view);
        songDao = new SongDao(this);
        verseparser = new VerseParser();
        loadSongs();

        final Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                vibrator.vibrate(15);
                songTitle = songListView.getItemAtPosition(position).toString();
                System.out.println("Selected title:"+songTitle);
                LayoutInflater li = LayoutInflater.from(ServiceSongListActivity.this);
                View promptsView = li.inflate(R.layout.service_delete_dialog, null);
                TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
                deleteMsg.setText("Do you want to delete the song?");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceSongListActivity.this);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        serviceFile = PropertyUtils.getPropertyFile(ServiceSongListActivity.this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
                        removeSong();
                        loadSongs();
                        Toast.makeText(ServiceSongListActivity.this, "Song " + songTitle + " Deleted...!", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });


        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                String selectedValue = songListView.getItemAtPosition(position).toString();
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
                {
                    verseListData = getVerseByVerseOrder(verseOrder);
                }
                Intent intent = new Intent(ServiceSongListActivity.this, SongsColumnViewActivity.class);
                intent.putExtra("serviceName", selectedValue);
                if(verseListData.size() > 0){
                    intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
                    for(int i=0; i<verseListData.size();i++){
                        verseListDataContent.add(verseDataMap.get(verseListData.get(i)));
                    }
                    intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
                    Log.d(this.getClass().getName(),"Verse List data content :"+ verseListDataContent);
                }
                else{
                    intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                    intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                }
                startActivity(intent);

            }
        });
    }

    private void initSetUp()
    {
        songDao.open();
        loadSongs();
        dataArray = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            dataArray[i] = songs.get(i).toString();
        }
    }

    private void loadSongs()
    {
        File serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(";");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, propertyValues);
        songListView.setAdapter(adapter);
    }

    private void removeSong() {
        try
        {
            String propertyValue = "";
            File serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            String property = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValues[] = property.split(";");
            System.out.println("File:"+serviceFile);
            for(int i = 0; i < propertyValues.length; i++)
            {
                if(!propertyValues[i].equalsIgnoreCase(songTitle))
                {
                    propertyValue = propertyValue+propertyValues[i]+";";
                }
            }
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }



    private List<Verse> getVerse(String lyrics)
    {
        return verseparser.parseVerseDom(this, lyrics);
    }

    private List<String> getVerseByVerseOrder(String verseOrder)
    {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i].toLowerCase());
        }
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
                adapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
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
}
