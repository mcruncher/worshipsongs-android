package org.worshipsongs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class PresentSongCardViewAdapter extends ArrayAdapter<String>
{
    private int selectedItem = -1;
    private final Context context;
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    public PresentSongCardViewAdapter(Context context, List<String> objects)
    {
        super(context, R.layout.present_song_card_view, objects);
        this.context = context;
        preferenceSettingService = new UserPreferenceSettingService();
        customTagColorService = new CustomTagColorService();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.present_song_card_view, null);
        }
        String verse = getItem(position);

        if (verse != null) {
            setTextView(position, view, verse);
        }
        return view;
    }

    private void setTextView(int position, View v, String verse)
    {
        TextView textView = (TextView) v.findViewById(R.id.verse_text_view);
        if (textView != null) {
            textView.setText("");
            customTagColorService.setCustomTagTextView(textView, verse, preferenceSettingService.getPrimaryColor(),
                    preferenceSettingService.getSecondaryColor());
            textView.setTextSize(preferenceSettingService.getPortraitFontSize());
            textView.setTextColor(preferenceSettingService.getPrimaryColor());
        }
        if (selectedItem == position) {
            textView.setBackgroundResource(R.color.gray);
        } else {
            textView.setBackgroundResource(R.color.white);

        }
        textView.setLineSpacing(0, 1.2f);
    }

    public void setItemSelected(int position)
    {
        selectedItem = position;
    }

    public int getSelectedItem()
    {
        return selectedItem;
    }
}


