package org.worshipsongs.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * author:madasamy
 * version:2.1.0
 */
public class SongContentFullViewFragment extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_full_view_fragment, container, false);
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        textView = (TextView) view.findViewById(R.id.text);
        Bundle bundle = getArguments();
        setText(bundle.getString("content"));
        return view;
    }

    public void setText(String songText)
    {
        textView.setText(songText);
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(this.getActivity(), text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }
}
