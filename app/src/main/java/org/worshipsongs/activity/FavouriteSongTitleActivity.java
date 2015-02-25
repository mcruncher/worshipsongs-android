package org.worshipsongs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

import org.worshipsongs.worship.R;

/**
 * Created by Pitchu on 2/12/2015.
 */
public class FavouriteSongTitleActivity extends Activity
{
    private ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);

        songListView = (ListView) findViewById(R.id.list_view);

    }
}
