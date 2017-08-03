package org.worshipsongs.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.worshipsongs.R;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;

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
        customTagColorService.setCustomTagTextView(textView, text, preferenceSettingService.getPrimaryColor(),
                preferenceSettingService.getSecondaryColor());
        textView.setTextSize(preferenceSettingService.getPortraitFontSize());
        textView.setTextColor(preferenceSettingService.getPrimaryColor());
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
