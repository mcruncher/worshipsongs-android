package org.worshipsongs.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.worshipsongs.domain.Song;
import org.worshipsongs.worship.R;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongsListActivity extends Activity
{
    private ListView songListView;
    private VerseParser verseparser;
    private SongDao songDao;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<Song> adapter;
    private String[] dataArray;
   // private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        songListView = (ListView) findViewById(R.id.list_view);
       // progressDialog = new ProgressDialog(this);
        songDao = new SongDao(this);
        verseparser = new VerseParser();
        try {
           // songDao.copyDatabase("", false);
        } catch (Exception ex) {
            Log.w(this.getClass().getName(), "Error occurred while creating database", ex);
        }
        initSetUp();

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Song selectedValue = adapter.getItem(position);
                String lyrics = selectedValue.getLyrics();
                String verseOrder = selectedValue.getVerseOrder();
                List<String> verseListData = new ArrayList<String>();
                Log.d("Verse order cursor: ", verseOrder);
                if(!verseOrder.isEmpty())
                {
                    verseListData = getVerseByVerseOrder(verseOrder);
                }
                getVerse(lyrics);
                List<String> verseName = new ArrayList<String>();
                List<String> verseContent = new ArrayList<String>();
                Map<String,String> verseData = new HashMap<String, String>();
                for (Verse verses : verseList) {
                    verseName.add(verses.getType() + verses.getLabel());
                    verseContent.add(verses.getContent());
                    verseData.put(verses.getType() + verses.getLabel(),verses.getContent());
                }
                List<String> verseListDataContent = new ArrayList<String>();
                Intent intent = new Intent(SongsListActivity.this, SongsColumnViewActivity.class);
                if(verseListData.size()>0){
                    intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
                    for(int i=0; i<verseListData.size();i++){
                        verseListDataContent.add(verseData.get(verseListData.get(i)));
                    }
                    intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
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
        initColor();
    }

    private void initColor()
    {
        SharedPreferences customSharedPreference = getSharedPreferences("myCustomSharedPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = customSharedPreference.edit();
        editor.putInt("color2", -16711936);
        editor.commit();
    }

    private void getVerse(String lyrics)
    {
        verseList = new ArrayList<Verse>();
        verseList = verseparser.parseVerseDom(this, lyrics);
    }

    private List<String> getVerseByVerseOrder(String verseOrder)
    {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i]);
        }
        Log.d("Verses list: ", verses.toString());
        return verses;
    }

    private void loadSongs()
    {
        songs = songDao.findTitles();
        adapter = new ArrayAdapter<Song>(this,
                android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SongsListActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(SongsListActivity.this, AboutWebViewActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
