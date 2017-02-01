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


    private final SparseArray<Presentation> activePresentations = new SparseArray<Presentation>();
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private DisplayManager displayManager;
    private Song song;
    // private AuthorSong authorSong;
    private Context context;



    public SongCardViewAdapter(Song song, Context context)
    {
        this.context = context;
        this.song = song;

        setDisplayManager();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setDisplayManager()
    {
        displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(new DisplayListener(), null);
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
        songContentViewHolder.cardView.setOnClickListener(new CardViewListener(position));
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
        textView.setSelected(selectedItems.get(position, false));
        // textView.setOnClickListener(new TextViewListener(textView, position));
    }

    private boolean isJellyBean()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
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


    private class CardViewListener implements View.OnClickListener
    {
        private int position;

        CardViewListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View view)
        {

            if (Setting.getInstance().getDisplay() != null) {
                // hidePresentation(display);
                showPresentation(position);
            }
        }
    }

    private class TextViewListener implements View.OnClickListener
    {
        private TextView textView;
        private int position;

        TextViewListener(TextView textView, int position)
        {
            this.textView = textView;
            this.position = position;
        }

        @Override
        public void onClick(View view)
        {
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
                textView.setSelected(false);
            } else {
                selectedItems.put(position, true);
                textView.setSelected(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class DisplayListener implements DisplayManager.DisplayListener
    {

        @Override
        public void onDisplayAdded(int displayId)
        {
//           Display display = getDisplay();
//           if (display != null) {
//               Setting.getInstance().setDisplay(display);
//           }
        }

        @Override
        public void onDisplayRemoved(int displayId)
        {
            Presentation presentation = activePresentations.get(displayId);
            if (presentation == null) {
                return;
            }
            // presentation.dismiss();
            activePresentations.delete(displayId);
        }

        @Override
        public void onDisplayChanged(int displayId)
        {

        }
    }

    private Display getDisplay()
    {
        Log.i(SongCardViewAdapter.class.getSimpleName(), "Is jelly bean " + isJellyBean());
        if (isJellyBean()) {
            Display[] displays = displayManager.getDisplays();
            Log.i(SongCardViewAdapter.class.getSimpleName(), "No of displays" + displays.length);
            for (Display display : displays) {
                Log.i(SongCardViewAdapter.class.getSimpleName(), "Display name " + display.getName());
                if (!display.getName().contains("Built-in Screen")) {
                    return display;
                }
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPresentation(int position)
    {
        setDisplay();
        if (isJellyBean()) {

            RemoteSongPresentation presentation = new RemoteSongPresentation(context, song, position);
            activePresentations.put(Setting.getInstance().getDisplay().getDisplayId(), presentation);
            presentation.show();
        }
        notifyDataSetChanged();
    }

    private void setDisplay()
    {
        Display display = getDisplay();
        if (display != null && Setting.getInstance().getDisplay() == null) {
            Setting.getInstance().setDisplay(display);
            // Toast.makeText(WorshipSongApplication.getContext(), "Tap song content to present a song in " + display.getName() + " display", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(WorshipSongApplication.getContext(), "Remote display not connected to this device", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void hidePresentation(Display display)
    {
        if (isJellyBean() && display != null) {
            DefaultRemotePresentation defaultRemotePresentation = new DefaultRemotePresentation(context, Setting.getInstance().getDisplay());
            activePresentations.put(Setting.getInstance().getDisplay().getDisplayId(), defaultRemotePresentation);
            defaultRemotePresentation.show();
        }

        notifyDataSetChanged();
    }



}
