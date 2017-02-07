package org.worshipsongs.adapter;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dialog.DefaultRemotePresentation;
import org.worshipsongs.dialog.RemoteSongPresentation;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

/**
 * author: Madasamy
 * version: 2.1.0
 */
public class SongCardViewAdapter extends RecyclerView.Adapter<SongCardViewAdapter.SongContentViewHolder>
{

    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private Song song;
    private Context context;

    public SongCardViewAdapter(Song song, Context context)
    {
        this.context = context;
        this.song = song;
    }

    @Override
    public int getItemCount()
    {
        return song.getContents().size();
    }

    @Override
    public void onBindViewHolder(SongContentViewHolder songContentViewHolder, int position)
    {
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        String verse = song.getContents().get(position);
        songContentViewHolder.textView.setText(verse);
        loadTextStyle(songContentViewHolder.textView, position);
    }

    private void loadTextStyle(TextView textView, int position)
    {
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(context, text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getPortraitFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }


    @Override
    public SongContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_content_view_card_layout, viewGroup, false);
        return new SongContentViewHolder(itemView);
    }

    static class SongContentViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textView;

        SongContentViewHolder(View view)
        {
            super(view);
            cardView = (CardView) view.findViewById(R.id.verse_card_view);
            textView = (TextView) view.findViewById(R.id.verse_text_view);
        }
    }
}
