package org.worshipsongs.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.SongsColumnViewActivity;
import org.worshipsongs.component.DropDownList;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Seenivasan on 5/9/2015.
 */
public class SongListAdapter extends BaseAdapter implements Filterable {

    private SongsFilter songsFilter;
    private LayoutInflater inflater;
    private List<Verse> verseList;
    private List<Song> songs;
    private ArrayList<Song> songList = new ArrayList<Song>();
    private WorshipSongApplication application = new WorshipSongApplication();
    private UtilitiesService utilitiesService = new UtilitiesService();
    private PopupWindow popupWindow;
    private ArrayAdapter<Song> adapter;
    private Song selectedSong;
    private CommonService commonService = new CommonService();
    private Context context;
    private DropDownList dropDownList = new DropDownList();

    public SongListAdapter(Context context, List<Song> songs, ArrayAdapter<Song> adapter) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.songs = songs;
        Song songClass;
        for (Song song : songs) {
            songClass = new Song();
            songClass.setTitle(song.getTitle());
            songList.add(songClass);
        }
        this.adapter = adapter;
    }

    public SongListAdapter() {

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.songs_listview_content, null);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(songList.get(position).getTitle().trim());
        final int temp = position;
        (convertView.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                selectedSong = adapter.getItem(temp);
                Log.d("Selected song:", selectedSong.getTitle());
                String lyrics = selectedSong.getLyrics();
                verseList = utilitiesService.getVerse(lyrics);
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
                String verseOrder = selectedSong.getVerseOrder();
                if (StringUtils.isNotBlank(verseOrder)) {
                    verseListData = utilitiesService.getVerseByVerseOrder(verseOrder);
                }
                Log.d(this.getClass().getName(), "Verse List data :" + verseListData);
                Log.d(this.getClass().getName(), "Verse List data sizze :" + verseListData.size());
                Intent intent = new Intent(application.getContext(), SongsColumnViewActivity.class);
                intent.putExtra("serviceName", selectedSong.getTitle());
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.getContext().startActivity(intent);
                //startActivity(intent);
            }
        });

        (convertView.findViewById(R.id.serviceIcon)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final Vibrator vibrator = (Vibrator) application.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(15);
                selectedSong = adapter.getItem(temp);
                createPopUpWindow();
                popupWindow.showAsDropDown(arg0, -5, 0);

            }
        });
        return convertView;
    }

    public int getCount() {
        return songList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (songsFilter == null)
            songsFilter = new SongsFilter();
        return songsFilter;
    }

    public void createPopUpWindow() {
        String popUpContents[];
        List<String> popUpList = new ArrayList<String>();
        popUpList.add("Add to playlist");
        popUpContents = new String[popUpList.size()];
        popUpList.toArray(popUpContents);
        System.out.println("Selected Song for Service:" + selectedSong);
        popupWindow = popupWindow(context, popUpContents);
    }

    public PopupWindow popupWindow(Context context, String popUpContents[]) {
        PopupWindow popupWindow1 = new PopupWindow(context);
        ListView listView = new ListView(context);
        listView.setAdapter(popUpAdapter(popUpContents));
        SongListAdapter listAdapter = new SongListAdapter();
        listView.setOnItemClickListener(new DropDownList(context, popupWindow1, selectedSong));
        popupWindow1.setFocusable(true);
        popupWindow1.setWidth(250);
        popupWindow1.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow1.setContentView(listView);
        return popupWindow1;
    }

    private ArrayAdapter<String> popUpAdapter(String popUpArray[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(application.getContext(), android.R.layout.simple_list_item_1, popUpArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(application.getContext());
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


    private class SongsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = songs;
                results.count = songs.size();
            } else {
                ArrayList<Song> filteredSongs = new ArrayList<Song>();
                for (Song s : songs) {
                    if (s.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filteredSongs.add(s);
                    }
                }
                results.values = filteredSongs;
                results.count = filteredSongs.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songList = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }
    }

//    public Song getSelectedSong()
//    {
//        System.out.println("Return Selected Song:" + selectedSong.toString());
//        return selectedSong;
//    }
}
