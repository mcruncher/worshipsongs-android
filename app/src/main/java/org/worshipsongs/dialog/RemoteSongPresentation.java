package org.worshipsongs.dialog;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.worshipsongs.R;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RemoteSongPresentation extends Presentation
{

    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private CustomTagColorService customTagColorService = new CustomTagColorService();

    // private Context context;
    private TextView songSlideTextView;
    private ImageView imageView;
    private ScrollView scrollView;
    private TextView verseTextView;
    private TextView songTitleTextView;
    private TextView authorNameTextView;

    public RemoteSongPresentation(Context context, Display display)
    {
        super(context, display);
        //this.context = context;
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
        setAuthorNameView();
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
        imageView.setBackground(new ColorDrawable(preferenceSettingService.getPresentationBackgroundColor()));
    }

    private void setScrollView()
    {
        scrollView = (ScrollView) findViewById(R.id.verse_land_scape_scrollview);
        setVerseVisibility(View.GONE);
    }

    public void setVerseVisibility(int visible)
    {
        scrollView.setVisibility(visible);
        scrollView.setBackground(new ColorDrawable(preferenceSettingService.getPresentationBackgroundColor()));
    }

    private void setVerseView()
    {
        verseTextView = (TextView) findViewById(R.id.text);
        verseTextView.setText("");
    }

    public void setVerse(String verse)
    {
        verseTextView.setText("");
        customTagColorService.setCustomTagTextView(verseTextView, verse, preferenceSettingService.getPresentationPrimaryColor(),
                preferenceSettingService.getPresentationSecondaryColor());
        verseTextView.setTextSize(preferenceSettingService.getLandScapeFontSize());
        // verseTextView.setTextColor(preferenceSettingService.getPrimaryColor());
        verseTextView.setVerticalScrollBarEnabled(true);
    }

    private void setSongTitleView()
    {
        songTitleTextView = (TextView) findViewById(R.id.song_title);
    }

    public void setSongTitleAndChord(String title, String chord, int color)
    {
        songTitleTextView.setText("");
        String formattedTitle = getResources().getString(R.string.title) +" " + title + " " + getChord(chord);
        songTitleTextView.setText(formattedTitle);
        songTitleTextView.setTextColor(color);
    }

    private String getChord(String chord)
    {

        if (chord != null && chord.length() > 0) {
            return " [" + chord + "]";
        }
        return "";
    }

    private void setAuthorNameView()
    {
        authorNameTextView = (TextView) findViewById(R.id.author_name);
    }

    public void setAuthorName(String authorName, int color)
    {
        authorNameTextView.setText("");
        String formattedAuthor = getResources().getString(R.string.author) + " " + authorName;
        authorNameTextView.setText(formattedAuthor);
        authorNameTextView.setTextColor(color);
    }

    private void setSongSlide()
    {
        songSlideTextView = (TextView) findViewById(R.id.song_slide);
    }

    public void setSlidePosition(int position, int size, int color)
    {
        songSlideTextView.setText("");
        String slidePosition = getResources().getString(R.string.slide) + " " + getSongSlideValue(position, size);
        songSlideTextView.setText(slidePosition);
        songSlideTextView.setTextColor(color);
    }

    private String getSongSlideValue(int currentPosition, int size)
    {
        int slidePosition = currentPosition + 1;
        return slidePosition + " of " + size;
    }

    @Override
    public void onDisplayRemoved()
    {
        super.onDisplayRemoved();
        Log.i(RemoteSongPresentation.class.getSimpleName(), "When display is removed");
    }
}
