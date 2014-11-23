package org.worshipsongs.adapter;


import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.worship.R;
import org.worshipsongs.service.UserPreferenceSettingService;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class CustomListViewAdapter extends BaseAdapter
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private Context context = WorshipSongApplication.getContext();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private LayoutInflater layoutInflater;

    public CustomListViewAdapter(Context context)
    {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item)
    {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item)
    {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public String getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        preferenceSettingService = new UserPreferenceSettingService();
        customTagColorService = new CustomTagColorService();
        int rowType = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = layoutInflater.inflate(R.layout.custom_list_view_adapter_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    //loadTextStyle(holder.textView);
                    String text = holder.textView.getText().toString();
                    Log.d(this.getClass().getName(), "Text content" + text);

                    break;
                case TYPE_SEPARATOR:
                    convertView = layoutInflater.inflate(R.layout.custom_list_view_adapter_item2, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
            convertView.setBackgroundResource(R.drawable.rounded_corner);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));
        loadTextStyle(holder.textView);
        return convertView;
    }

    private void loadTextStyle(TextView textView)
    {
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        String text = textView.getText().toString();
        textView.setText("");
        Log.d(this.getClass().getName(), "Text content" + text);
        customTagColorService.setCustomTagTextView(context, text, textView);
        textView.setTypeface(preferenceSettingService.getTypeFace(), preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    public static class ViewHolder
    {
        public TextView textView;
    }
}
