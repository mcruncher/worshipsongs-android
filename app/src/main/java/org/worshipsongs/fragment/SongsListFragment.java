package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
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
    private List<String> serviceList = new ArrayList<String>();
    private File serviceFile = null;
    private String song;
    AlertDialog alertDialog;
    private Song songClass;
    public ArrayList<Song> songList = new ArrayList<Song>();
    ListAdapter listAdapter;
    ServiceListAdapter serviceListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity = (FragmentActivity) super.getActivity();
        FragentLayout = (LinearLayout) inflater.inflate(R.layout.songs_list_activity, container, false);
        setHasOptionsMenu(true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        userPreferenceSettingService = new UserPreferenceSettingService();
        songListView = (ListView) FragentLayout.findViewById(R.id.song_list_view);
        songDao = new SongDao(getActivity());
        verseparser = new VerseParser();
        initSetUp();
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
        for (Song song : songs) {
            songClass = new Song();
            songClass.setTitle(song.getTitle());
            songList.add(songClass);
        }
        adapter = new ArrayAdapter<Song>(getActivity(), android.R.layout.simple_list_item_1, songs);
        listAdapter = new ListAdapter(getActivity());
        songListView.setAdapter(listAdapter);
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
            title.setText(songList.get(position).getTitle().trim());
            final int temp = position;
            (convertView.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    Song selectedValue = adapter.getItem(temp);
                    String lyrics = selectedValue.getLyrics();
                    verseList = getVerse(lyrics);
                    List<String> verseName = new ArrayList<String>();
                    List<String> verseContent = new ArrayList<String>();
                    Map<String, String> verseDataMap = new HashMap<String, String>();
                    for (Verse verses : verseList) {
                        verseName.add(verses.getType() + verses.getLabel());
                        verseContent.add(verses.getContent());
                        verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
                    }
                    Log.d(this.getClass().getName(), "Verse Name :" + verseName);
                    Log.d(this.getClass().getName(), "Verse Content :" + verseName);
                    Log.d(this.getClass().getName(), "Verse Data map :" + verseDataMap);
                    List<String> verseListDataContent = new ArrayList<String>();
                    List<String> verseListData = new ArrayList<String>();
                    String verseOrder = selectedValue.getVerseOrder();
                    if (StringUtils.isNotBlank(verseOrder)) {
                        verseListData = getVerseByVerseOrder(verseOrder);
                    }
                    Log.d(this.getClass().getName(), "Verse List data :" + verseListData);
                    Log.d(this.getClass().getName(), "Verse List data sizze :" + verseListData.size());
                    Intent intent = new Intent(getActivity().getApplication(), SongsColumnViewActivity.class);
                    intent.putExtra("serviceName", selectedValue.getTitle());
                    if (verseListData.size() > 0) {
                        intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseListData);
                        for (int i = 0; i < verseListData.size(); i++) {
                            verseListDataContent.add(verseDataMap.get(verseListData.get(i)));
                        }
                        intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseListDataContent);
                        Log.d(this.getClass().getName(), "Verse List data content :" + verseListDataContent);
                    } else {
                        Log.d(this.getClass().getName(), "Else Part :");
                        Log.d(this.getClass().getName(), "Verse Name :" + verseName);
                        Log.d(this.getClass().getName(), "Verse Content :" + verseName);
                        intent.putStringArrayListExtra("verseName", (ArrayList<String>) verseName);
                        intent.putStringArrayListExtra("verseContent", (ArrayList<String>) verseContent);
                    }
                    startActivity(intent);
                }
            });

            (convertView.findViewById(R.id.serviceIcon)).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(15);
                    final Song selectedSong = adapter.getItem(temp);
                    //song = songListView.getItemAtPosition(temp).toString();
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.service_name_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptsView);
                    final TextView title = (TextView) promptsView.findViewById(R.id.songTitle);
                    final ListView serviceListView = (ListView) promptsView.findViewById(R.id.service_list);
                    title.setText("Add to service");
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                    serviceList.clear();
                    serviceList.add("New service...");
                    serviceList = readServiceName();
                    dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.service_alertdialog_content, serviceList);
                    serviceListAdapter = new ServiceListAdapter(getActivity());
                    serviceListView.setAdapter(serviceListAdapter);
                    serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                        {
                            //String service = serviceListView.getItemAtPosition(position).toString();
                            String service = dataAdapter.getItem(position);
                            System.out.println("Selected Song for Service:" + service);
                            if (position == 0) {
                                LayoutInflater li = LayoutInflater.from(getActivity());
                                View promptsView = li.inflate(R.layout.add_service_dialog, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setView(promptsView);
                                final TextView textViewServiceName = (TextView) promptsView.findViewById(R.id.textViewServiceName);
                                textViewServiceName.setTypeface(Typeface.DEFAULT_BOLD);
                                final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
                                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        String service_name;
                                        if (serviceName.getText().toString().equals(""))
                                            Toast.makeText(getActivity(), "Enter Service Name...!", Toast.LENGTH_LONG).show();
                                        else {
                                            service_name = serviceName.getText().toString();
                                            saveIntoFile(service_name, selectedSong.toString());
                                            Toast.makeText(getActivity(), "Song added to service...!", Toast.LENGTH_LONG).show();
                                            alertDialog.dismiss();
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                saveIntoFile(service, selectedSong.toString());
                                Toast.makeText(getActivity(), "Song added to service...!", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        }
                    });

                    alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            return convertView;
        }

        public int getCount()
        {
            return songList.size();
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
                    results.values = songs;
                    results.count = songs.size();
                } else {
                    ArrayList<Song> filteredContacts = new ArrayList<Song>();
                    for (Song s : songs) {
                        if (s.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filteredContacts.add(s);
                        }
                    }
                    results.values = filteredContacts;
                    results.count = filteredContacts.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                songList = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            }
        }
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
        super.onCreateOptionsMenu(menu, inflater);
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
        try {
            serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
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
            serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
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
}