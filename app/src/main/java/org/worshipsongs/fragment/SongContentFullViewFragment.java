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

import org.worshipsongs.component.SwipeTouchListener;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentFullViewFragment extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private TextView textView;
    private int currentPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_full_view_fragment, container, false);
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        textView = (TextView) view.findViewById(R.id.text);
        Bundle bundle = getArguments();
        // setText(bundle.getString("content"));
        setText(bundle.getStringArrayList("content"));
        return view;
    }

    private void setText(final ArrayList<String> content)
    {
        //textView.setAnimation();
        textView.setOnTouchListener(new SwipeTouchListener(SongContentFullViewFragment.this.getActivity())
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
                        textView.setAnimation(AnimationUtils.loadAnimation(SongContentFullViewFragment.this.getActivity().getApplicationContext()
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
