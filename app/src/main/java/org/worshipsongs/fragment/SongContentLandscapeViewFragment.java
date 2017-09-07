package org.worshipsongs.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentLandscapeViewFragment extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_landscape_view_fragment, container, false);
        hideStatusBar();
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        Bundle bundle = getArguments();
        String title = bundle.getString(CommonConstants.TITLE_KEY);
        String content = bundle.getString("content");
        String authorName = bundle.getString("authorName");
        String position = bundle.getString("position");
        String size = bundle.getString("size");
        String chord = bundle.getString("chord");
        setScrollView(view);
        setContent(content, view);
        setSongTitle(view, title, chord);
        setAuthorName(view, authorName);
        setSongSlide(view, position, size);
        return view;
    }

    private void hideStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setScrollView(View view)
    {
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.verse_land_scape_scrollview);
        scrollView.setBackgroundColor(preferenceSettingService.getPresentationBackgroundColor());
    }

    private void setContent(String content, View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(content);
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(textView, text, preferenceSettingService.getPresentationPrimaryColor(),
                preferenceSettingService.getPresentationSecondaryColor());
        textView.setTextSize(preferenceSettingService.getLandScapeFontSize());
        textView.setTextColor(preferenceSettingService.getPrimaryColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    private void setSongTitle(View view, String title, String chord)
    {
        TextView songTitleTextView = (TextView) view.findViewById(R.id.song_title);
        String formattedTitle = getResources().getString(R.string.title) + " " + title + " " + getChord(chord);
        songTitleTextView.setText(formattedTitle);
        songTitleTextView.setTextColor(preferenceSettingService.getPresentationPrimaryColor());
    }

    private String getChord(String chord)
    {

        if (chord != null && chord.length() > 0) {
            return " [" + chord + "]";
        }
        return "";
    }

    private void setAuthorName(View view, String authorName)
    {
        TextView authorNameTextView = (TextView) view.findViewById(R.id.author_name);
        String formattedAuthor = getResources().getString(R.string.author) + " " + authorName;
        authorNameTextView.setText(formattedAuthor);
        authorNameTextView.setTextColor(preferenceSettingService.getPresentationPrimaryColor());
    }

    private void setSongSlide(View view, String position, String size)
    {
        TextView songSlideTextView = (TextView) view.findViewById(R.id.song_slide);
        String slidePosition = getResources().getString(R.string.slide) + " " + getSongSlideValue(position, size);
        songSlideTextView.setText(slidePosition);
        songSlideTextView.setTextColor(preferenceSettingService.getPresentationPrimaryColor());
    }

    private String getSongSlideValue(String currentPosition, String size)
    {
        int slidePosition = Integer.parseInt(currentPosition) + 1;
        return slidePosition + " of " + size;
    }
}
