package org.worshipsongs.page.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class VerseContentView extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService = new CustomTagColorService();
    ;
    private Context context = WorshipSongApplication.getContext();
    private TextView textView;
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab_content_view, container, false);
        textView = ((TextView) rootView.findViewById(R.id.data));
        preferenceSettingService = new UserPreferenceSettingService();
        if (getArguments() != null) {
            text = getArguments().getString("verseData");
            customTagColorService.setCustomTagTextView(context, text, textView);
            textView.setTypeface(preferenceSettingService.getTypeFace(), preferenceSettingService.getFontStyle());
            textView.setTextSize(preferenceSettingService.getFontSize());
            textView.setVerticalScrollBarEnabled(true);
            //textView.setTextColor(preferenceSettingService.getColor());
            Log.d("VerseContentView", "preferenceSettingService.getColor: " + preferenceSettingService.getColor());
            textView.setTextColor(preferenceSettingService.getColor());
        }
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        textView.setTypeface(preferenceSettingService.getTypeFace(), preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }
}