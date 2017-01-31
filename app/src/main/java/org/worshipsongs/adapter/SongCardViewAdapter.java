package org.worshipsongs.adapter;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.AuthorSongDao;
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

    private AuthorSongDao authorSongDao = new AuthorSongDao(WorshipSongApplication.getContext());
    private final SparseArray<RemotePresentation> activePresentations = new SparseArray<RemotePresentation>();
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    private Song song;
    private AuthorSong authorSong;
    private Context context;

    public SongCardViewAdapter(Song song, Context context)
    {
        this.context = context;
        this.song = song;
        authorSong = authorSongDao.findByTitle(song.getTitle());
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
        loadTextStyle(songContentViewHolder.textView);
    }

    private void loadTextStyle(TextView textView)
    {

        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(context, text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getPortraitFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
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
                //hidePresentation(display);
                showPresentation(position);
            }
        }

        private void hidePresentation(Display display)
        {
            if (isJellyBean()) {
                final int displayId = display.getDisplayId();
                RemotePresentation presentation = activePresentations.get(displayId);
                if (presentation == null) {
                    return;
                }
                presentation.dismiss();
                activePresentations.delete(displayId);
            }
        }
    }

    public void showPresentation(int position)
    {
        if (isJellyBean()) {
            String verse = song.getContents().get(position);
            RemotePresentation presentation = new RemotePresentation(Setting.getInstance().getDisplay(), verse, position);
            activePresentations.put(Setting.getInstance().getDisplay().getDisplayId(), presentation);
            presentation.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private final class RemotePresentation extends Presentation
    {
        private String verse;
        private int position;

        RemotePresentation(Display display, String verse, int position)
        {
            super(context, display);
            this.verse = verse;
            this.position = position;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.song_content_landscape_view_fragment);
            setContent(verse);
            setSongTitle(song.getTitle(), song.getChord());
            setAuthorName(authorSong.getAuthor().getDisplayName());
            setSongSlide(position, song.getContents().size());
        }

        private void setContent(String content)
        {
            TextView textView = (TextView)findViewById(R.id.text);
            textView.setText(content);
            String text = textView.getText().toString();
            textView.setText("");
            customTagColorService.setCustomTagTextView(context, text, textView);
            textView.setTypeface(preferenceSettingService.getFontStyle());
            textView.setTextSize(preferenceSettingService.getLandScapeFontSize());
            textView.setTextColor(preferenceSettingService.getColor());
            textView.setVerticalScrollBarEnabled(true);
        }

        private void setSongTitle(String title, String chord)
        {
            TextView songTitleTextView = (TextView) findViewById(R.id.song_title);
            songTitleTextView.setText(" " + title + getChord(chord));
        }

        private String getChord(String chord)
        {

            if (chord != null && chord.length() > 0) {
                return " [" + chord + "]";
            }
            return "";
        }

        private void setAuthorName(String authorName)
        {
            TextView authorNameTextView = (TextView) findViewById(R.id.author_name);

            authorNameTextView.setText(" " + authorName);
        }

        private void setSongSlide(int position, int size)
        {
            TextView songSlideTextView = (TextView)findViewById(R.id.song_slide);
            songSlideTextView.setText(getSongSlideValue(position, size));
        }

        private String getSongSlideValue(int currentPosition, int size)
        {
            int slidePosition = currentPosition + 1;
            return " "+slidePosition + " of " + size;
        }
    }

}
