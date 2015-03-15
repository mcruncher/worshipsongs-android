package org.worshipsongs.page.component.fragment;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.activity.MainActivity;
import org.worshipsongs.activity.SongsColumnViewActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.service.UserPreferenceSettingService;
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
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SongsListFragment extends Fragment
{
    private ListView songListView;
    private VerseParser verseparser;
    private SongDao songDao;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<Song> adapter;
    private String[] dataArray;
    private UserPreferenceSettingService userPreferenceSettingService;
    ArrayAdapter<String> dataAdapter;

    private LinearLayout FragentLayout;
    private FragmentActivity FragmentActivity;
    private List<String> service = new ArrayList<String>();
    ServiceListFragment serviceListFragment = new ServiceListFragment();
    private File serviceFile = null;
    private String song;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity  = (FragmentActivity)    super.getActivity();
        FragentLayout = (LinearLayout) inflater.inflate(R.layout.songs_list_activity, container, false);
        setHasOptionsMenu(true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        userPreferenceSettingService = new UserPreferenceSettingService();

        songListView = (ListView) FragentLayout.findViewById(R.id.song_list_view);
        songDao = new SongDao(getActivity());
        verseparser = new VerseParser();
        initSetUp();

        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                song = songListView.getItemAtPosition(position).toString();
                System.out.println("Selected Song for Service:"+song);

                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.service_name_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);

                final TextView title = (TextView) promptsView.findViewById(R.id.songTitle);
                final ListView serviceListView = (ListView) promptsView.findViewById(R.id.service_list);

                //title.setText(song);
                title.setText("Select a service to add song");
                service.add("Click to create new service");
                service = readServiceName();

                dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, service);
                serviceListView.setAdapter(dataAdapter);

                serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                    {
                        String service = serviceListView.getItemAtPosition(position).toString();
                        System.out.println("Selected Song for Service:"+service);

                        if(position == 0)
                        {
                            LayoutInflater li = LayoutInflater.from(getActivity());
                            View promptsView = li.inflate(R.layout.add_service_dialog, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setView(promptsView);
                            final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener()
                            {

                                public void onClick(DialogInterface dialog,int id)
                                {
                                    String service_name;
                                    if (serviceName.getText().toString().equals(""))
                                        Toast.makeText(getActivity(), "Enter Service Name...!", Toast.LENGTH_LONG).show();
                                    else
                                    {
                                        service_name = serviceName.getText().toString();
                                        saveIntoFile(service_name, song);
                                        Toast.makeText(getActivity(), "Song added to favourites...!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                    }
                                }
                            }).setNegativeButton("Cancel",new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int id)
                                {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        else
                        {
                            saveIntoFile(service, song);
                            Toast.makeText(getActivity(), "Song added to service...!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
                alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        service.clear();
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Song selectedValue = adapter.getItem(position);
                String lyrics = selectedValue.getLyrics();
                verseList = getVerse(lyrics);

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
                String verseOrder = selectedValue.getVerseOrder();
                if(StringUtils.isNotBlank(verseOrder))
                {
                    verseListData = getVerseByVerseOrder(verseOrder);
                }
                Log.d(this.getClass().getName(),"Verse List data :"+ verseListData);
                Log.d(this.getClass().getName(),"Verse List data sizze :"+ verseListData.size());

                Intent intent = new Intent(getActivity().getApplication(), SongsColumnViewActivity.class);
                intent.putExtra("serviceName", selectedValue.getTitle());
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
        return FragentLayout;
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

    private List<Verse> getVerse(String lyrics)
    {
        return verseparser.parseVerseDom(getActivity(), lyrics);
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

    private void loadSongs()
    {
        songs = songDao.findTitles();
        adapter = new ArrayAdapter<Song>(getActivity(), android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater = new MenuInflater(getActivity().getApplicationContext());
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) this.FragmentActivity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.FragmentActivity.getComponentName()));
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
        super.onCreateOptionsMenu(menu,inflater);

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public List readServiceName()
    {
        Properties property = new Properties();
        InputStream inputStream = null;
        int i = 0;
        try
        {
            serviceFile = PropertyUtils.getServicePropertyFile(getActivity());
            inputStream = new FileInputStream(serviceFile);
            property.load(inputStream);

            Enumeration<?> e = property.propertyNames();
            while (e.hasMoreElements())
            {
                String key = (String) e.nextElement();
                //String value = prop.getProperty(key);
                service.add(key);
            }
            inputStream.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return service;
    }

    private void saveIntoFile(String serviceName, String song) {
        try
        {
            serviceFile = PropertyUtils.getServicePropertyFile(getActivity());

            System.out.println("FilePath:" + serviceFile);

            if (!serviceFile.exists()) {
                FileUtils.touch(serviceFile);
            }

            String existingProperty = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValue = "";
            if(StringUtils.isNotBlank(existingProperty))
            {
                propertyValue = existingProperty+";"+song;
            }else{
                propertyValue =song;
            }
            PropertyUtils.setServiceProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }
}