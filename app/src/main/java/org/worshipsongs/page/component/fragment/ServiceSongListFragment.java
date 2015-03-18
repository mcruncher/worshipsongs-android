package org.worshipsongs.page.component.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.activity.SongsColumnViewActivity;
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
 * Created by Seenivasan on 3/15/2015.
 */
public class ServiceSongListFragment extends Fragment {
    private ListView songListView;
    private VerseParser verseparser;
    private List<Song> songs;
    private List<Verse> verseList;
    private ArrayAdapter<String> adapter;
    private String[] dataArray;
    private LinearLayout FragentLayout;
    private android.support.v4.app.FragmentActivity FragmentActivity;
    private String serviceName;
    private String songTitle;
    private SongDao songDao;
    private Song song;
    private File serviceFile = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        serviceName=getArguments().getString("serviceName");
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(serviceName);
        FragmentActivity  = (FragmentActivity)    super.getActivity();
        FragentLayout = (LinearLayout) inflater.inflate(R.layout.songs_list_activity, container, false);
        songListView = (ListView) FragentLayout.findViewById(R.id.song_list_view);
        songDao = new SongDao(getActivity());
        verseparser = new VerseParser();
        loadSongs();
        final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                vibrator.vibrate(15);
                songTitle = songListView.getItemAtPosition(position).toString();
                System.out.println("Selected title:"+songTitle);
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.service_delete_dialog, null);
                TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
                deleteMsg.setText("Are you sure, you want to delete the song?");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        serviceFile = PropertyUtils.getServicePropertyFile(getActivity());
                        removeSong();
                        loadSongs();
                        Toast.makeText(getActivity(), "Song " + songTitle + " Deleted...!", Toast.LENGTH_LONG).show();
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
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
                Intent intent = new Intent(getActivity().getApplication(), SongsColumnViewActivity.class);
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
        return verses;
    }

    private void loadSongs()
    {
        File serviceFile = PropertyUtils.getServicePropertyFile(getActivity());
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(";");

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, propertyValues);
        songListView.setAdapter(adapter);
    }

    private void removeSong() {
        try
        {
            String propertyValue = "";
            File serviceFile = PropertyUtils.getServicePropertyFile(getActivity());
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
            PropertyUtils.setServiceProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }
}