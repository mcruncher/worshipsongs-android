package org.worshipsongs.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.component.SwipeTouchListener;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentLandscapeViewFragment extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private TextView textView;
    private int currentPosition;
    private WorshipSongApplication application = new WorshipSongApplication();
    private SongDao songDao = new SongDao(application.getContext());
    private AuthorSongDao authorSongDao = new AuthorSongDao(application.getContext());
    private TextView songSlideTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_landscape_view_fragment, container, false);
        hideStatusBar();
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        textView = (TextView) view.findViewById(R.id.text);
        Bundle bundle = getArguments();
        String title = bundle.getString(CommonConstants.TITLE_KEY);
        List<String> contents = songDao.findContentsByTitle(title);
        setText(contents);
        AuthorSong authorSong = authorSongDao.findByTitle(title);
        Log.i(this.getClass().getSimpleName(), "Author song " + authorSong);
        setSongTitle(view, title);
        setAuthorName(view, authorSong);
        setSongSlide(view, contents.size());
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

    private void setText(final List<String> content)
    {
        textView.setOnTouchListener(new SwipeTouchListener(SongContentLandscapeViewFragment.this.getActivity())
        {
            @Override
            public void onTopToBottomSwipe()
            {
                if (content != null && !content.isEmpty()) {
                    if (currentPosition >= 0) {
                        setContent(content.get(currentPosition), textView);
                        if (currentPosition != 0) {
                            currentPosition = currentPosition - 1;
                        }
                        songSlideTextView.setText(getSongSlideValue(currentPosition, content.size()));
                    }
                }
            }

            @Override
            public void onBottomToTopSwipe()
            {
                if (content != null && !content.isEmpty()) {
                    if (currentPosition <= (content.size() - 1)) {
                        textView.setAnimation(AnimationUtils.loadAnimation(SongContentLandscapeViewFragment.this.getActivity().getApplicationContext()
                                , android.R.anim.fade_out));
                        setContent(content.get(currentPosition == 0 ? currentPosition + 1 : currentPosition), textView);
                        if (currentPosition != (content.size() - 1)) {
                            currentPosition = currentPosition + 1;
                        }
                        songSlideTextView.setText(getSongSlideValue(currentPosition, content.size()));
                    }
                }
            }
        });
        setContent(content.get(currentPosition), textView);
    }

    private void setContent(String content, TextView textView)
    {
        textView.setText(content);
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(this.getActivity(), text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    private void setSongTitle(View view, String title)
    {
        TextView songTitleTextView = (TextView) view.findViewById(R.id.song_title);
        songTitleTextView.setText(title);
    }

    private void setAuthorName(View view, AuthorSong authorSong)
    {
        TextView authorNameTextView = (TextView) view.findViewById(R.id.author_name);
        authorNameTextView.setText(authorSong.getAuthor().getDisplayName());
    }

    private void setSongSlide(View view, int size)
    {
        songSlideTextView = (TextView) view.findViewById(R.id.song_slide);
        songSlideTextView.setText(getSongSlideValue(0, size));
    }

    private String getSongSlideValue(int currentPosition, int size)
    {
        int slidePosition = currentPosition + 1;
        return slidePosition + " of " + size;
    }
}
