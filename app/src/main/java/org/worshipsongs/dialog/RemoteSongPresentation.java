package org.worshipsongs.dialog;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
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
 * Author : Madasamy
 * Version : 2.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RemoteSongPresentation extends Presentation
{
    private AuthorSongDao authorSongDao = new AuthorSongDao(WorshipSongApplication.getContext());
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private CustomTagColorService customTagColorService = new CustomTagColorService();

    private Song song;
    private Context context;
    private String verse;
    private int position;
    private AuthorSong authorSong;

    public RemoteSongPresentation(Context context, Song song, int position)
    {
        super(context, Setting.getInstance().getDisplay());
        this.context = context;
        this.song = song;
        this.verse = song.getContents().get(position);
        this.position = position;
        authorSong = authorSongDao.findByTitle(song.getTitle());
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
        TextView textView = (TextView) findViewById(R.id.text);
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
        TextView songSlideTextView = (TextView) findViewById(R.id.song_slide);
        songSlideTextView.setText(getSongSlideValue(position, size));
    }

    private String getSongSlideValue(int currentPosition, int size)
    {
        int slidePosition = currentPosition + 1;
        return " " + slidePosition + " of " + size;
    }
}
