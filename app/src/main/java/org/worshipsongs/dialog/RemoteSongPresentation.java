package org.worshipsongs.dialog;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
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

    private Context context;
    private AuthorSong authorSong;
    private TextView songSlideTextView;
    private ImageView imageView;
    private ScrollView scrollView;
    private TextView verseTextView;
    private TextView songTitleTextView;
    private TextView authorNameTextView;

    public RemoteSongPresentation(Context context, Display display, String title)
    {
        super(context, display);
        this.context = context;
        if (title.length() != 0) {
            authorSong = authorSongDao.findByTitle(title);
        } else {
            authorSong = new AuthorSong();
            Author author = new Author();
            author.setDisplayName("");
            authorSong.setAuthor(author);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_content_landscape_view_fragment);
        setImageView();
        setScrollView();
        setVerseView();
        setSongTitleView();
        setAuthorNameView(authorSong.getAuthor().getDisplayName());
        setSongSlide();
    }

    private void setImageView()
    {
        imageView = (ImageView) findViewById(R.id.logo_image_view);
        setImageViewVisibility(View.VISIBLE);
    }

    public void setImageViewVisibility(int visible)
    {
        imageView.setVisibility(visible);
    }

    private void setScrollView()
    {
        scrollView = (ScrollView) findViewById(R.id.verse_land_scape_scrollview);
        setVerseVisibility(View.GONE);
    }

    public void setVerseVisibility(int visible)
    {
        scrollView.setVisibility(visible);
    }

    public void setVerseView()
    {
        verseTextView = (TextView) findViewById(R.id.text);
        verseTextView.setText("");
    }

    public void setVerse(String verse)
    {
        verseTextView.setText("");
        customTagColorService.setCustomTagTextView(context, verse, verseTextView);
        verseTextView.setTypeface(preferenceSettingService.getFontStyle());
        verseTextView.setTextSize(preferenceSettingService.getLandScapeFontSize());
        verseTextView.setTextColor(preferenceSettingService.getColor());
        verseTextView.setVerticalScrollBarEnabled(true);
    }

    private void setSongTitleView()
    {
        songTitleTextView = (TextView) findViewById(R.id.song_title);
    }

    public void setSongTitleAndChord(String title, String chord)
    {
        songTitleTextView.setText("");
        songTitleTextView.setText(" " + title + getChord(chord));
    }

    private String getChord(String chord)
    {

        if (chord != null && chord.length() > 0) {
            return " [" + chord + "]";
        }
        return "";
    }

    private void setAuthorNameView(String authorName)
    {
        authorNameTextView = (TextView) findViewById(R.id.author_name);
        setAuthorName("");
    }

    public void setAuthorName(String authorName)
    {
        authorNameTextView.setText("");
        authorNameTextView.setText(" " + authorName);
    }

    private void setSongSlide()
    {
        songSlideTextView = (TextView) findViewById(R.id.song_slide);
    }

    public void setSlidePosition(int position, int size)
    {
        songSlideTextView.setText("");
        songSlideTextView.setText(getSongSlideValue(position, size));
    }

    private String getSongSlideValue(int currentPosition, int size)
    {
        int slidePosition = currentPosition + 1;
        return " " + slidePosition + " of " + size;
    }

    @Override
    public void onDisplayRemoved()
    {
        super.onDisplayRemoved();
        Log.i(RemoteSongPresentation.class.getSimpleName(), "When display is removed");
    }
}
