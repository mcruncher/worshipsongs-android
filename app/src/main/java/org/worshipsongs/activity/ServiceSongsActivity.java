package org.worshipsongs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
//import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Service;
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
 * Created by Pitchu on 3/10/2015.
 */
public class ServiceSongsActivity extends Activity
{
    private ListView serviceListView;
    private File serviceFile = null;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Song> songAdapter;
    String serviceName;
    final Context context = this;
    private List<Verse> verseList;
    private VerseParser verseparser;
    private String selectedTitle;
    SongDao songDao;
    Song song;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_list_activity);
        serviceListView = (ListView) findViewById(R.id.list_view);
        verseparser = new VerseParser();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serviceName = extras.getString("serviceName");
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(serviceName);
        loadTitle();
        songDao = new SongDao(context);
        song = new Song();

        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView < ? > parent, View view, int position, long id)
            {
                selectedTitle = serviceListView.getItemAtPosition(position).toString();

                song = songDao.getSongByTitle(selectedTitle);
                String lyrics = song.getLyrics();

                verseList = getVerse(lyrics);
                Log.d(this.getClass().getName(),"selectedTitle :"+ selectedTitle);
                Log.d(this.getClass().getName(),"lyrics :"+ lyrics);
                Log.d(this.getClass().getName(),"verseList :"+ verseList);

                Log.d(this.getClass().getName(),"Song dao :"+ songDao);

                List<String> verseName = new ArrayList<String>();
                List<String> verseContent = new ArrayList<String>();
                Map<String,String> verseDataMap = new HashMap<String, String>();
                for (Verse verses : verseList) {
                    verseName.add(verses.getType() + verses.getLabel());
                    verseContent.add(verses.getContent());
                    verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
                }
                Log.d(this.getClass().getName(),"Verse Name :"+ verseName);
                Log.d(this.getClass().getName(),"Verse Content :"+ verseName);
                Log.d(this.getClass().getName(),"Verse Data map :"+ verseDataMap);

                List<String> verseListDataContent = new ArrayList<String>();
                List<String> verseListData = new ArrayList<String>();
                String verseOrder = song.getVerseOrder();
                if(StringUtils.isNotBlank(verseOrder))
                {
                    verseListData = getVerseByVerseOrder(verseOrder);
                }
                Log.d(this.getClass().getName(),"Verse List data :"+ verseListData);
                Log.d(this.getClass().getName(),"Verse List data sizze :"+ verseListData.size());

                Intent intent = new Intent(context, SongsColumnViewActivity.class);
                if(verseListData.size() > 0){
                    intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
                    for(int i=0; i<verseListData.size();i++){
                        verseListDataContent.add(verseDataMap.get(verseListData.get(i)));
                    }
                    intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
                    Log.d(this.getClass().getName(),"Verse List data content :"+ verseListDataContent);
                }
                else{
                    Log.d(this.getClass().getName(),"Else Part :");
                    Log.d(this.getClass().getName(),"Verse Name :"+ verseName);
                    Log.d(this.getClass().getName(),"Verse Content :"+ verseName);
                    intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                    intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(this, SongsViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("selectedPage", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadTitle()
    {
        serviceFile = PropertyUtils.getServicePropertyFile(this);
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(",");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, propertyValues);
        serviceListView.setAdapter(adapter);
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
        Log.d("Verses list: ", verses.toString());
        return verses;
    }
}