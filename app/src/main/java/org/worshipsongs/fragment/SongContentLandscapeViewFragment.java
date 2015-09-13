package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.component.SwipeTouchListener;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.service.UtilitiesService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UtilitiesService utilitiesService = new UtilitiesService();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_landscape_view_fragment, container, false);
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        textView = (TextView) view.findViewById(R.id.text);
        Bundle bundle = getArguments();
        setText(getContents(bundle.getString(CommonConstants.TITLE_KEY)));
        return view;
    }

    ArrayList<String> getContents(String title)
    {
        Song song = songDao.getSongByTitle(title);
        String lyrics = song.getLyrics();
        ArrayList<String> contents = new ArrayList<>();
        List<Verse> verseList = utilitiesService.getVerse(lyrics);
        List<String> verseName = new ArrayList<String>();
        List<String> contentsByDefaultOrder = new ArrayList<String>();
        Map<String, String> verseDataMap = new HashMap<String, String>();
        for (Verse verses : verseList) {
            verseName.add(verses.getType() + verses.getLabel());
            contentsByDefaultOrder.add(verses.getContent());
            verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
        }
        List<String> contentsByVerseOrder = new ArrayList<String>();
        List<String> verseOrderList = new ArrayList<String>();
        String verseOrder = song.getVerseOrder();
        if (StringUtils.isNotBlank(verseOrder)) {
            verseOrderList = utilitiesService.getVerseByVerseOrder(verseOrder);
        }

        if (verseOrderList.size() > 0) {
            for (int i = 0; i < verseOrderList.size(); i++) {
                contentsByVerseOrder.add(verseDataMap.get(verseOrderList.get(i)));
            }
            contents.addAll(contentsByVerseOrder);
            Log.d(this.getClass().getName(), "Verse List data content :" + contentsByVerseOrder);
        } else {
            contents.addAll(contentsByDefaultOrder);
        }
        return contents;
    }

    private void setText(final ArrayList<String> content)
    {
        //textView.setAnimation();
        textView.setOnTouchListener(new SwipeTouchListener(SongContentLandscapeViewFragment.this.getActivity())
        {
            @Override
            public void onTopToBottomSwipe()
            {
                if (content != null && !content.isEmpty()) {
                    if (currentPosition >= 0) {
                        Log.i(this.getClass().getSimpleName(), "Current position: " + currentPosition);
                        setContent(content.get(currentPosition), textView);
                        if (currentPosition != 0) {
                            currentPosition = currentPosition - 1;
                        }
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
}
