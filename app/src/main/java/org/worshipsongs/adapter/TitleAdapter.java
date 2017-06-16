package org.worshipsongs.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.domain.AbstractDomain;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class TitleAdapter<E> extends ArrayAdapter<E>
{
    private String type;
    private int selectedItem = -1;
    private Activity activity;

    public TitleAdapter(@NonNull Activity activity, List<E> objects)
    {
        super(activity, R.layout.songs_listview_content, objects);
        this.activity = activity;
    }

    public TitleAdapter(@NonNull Activity activity, List<E> objects, String type)
    {
        super(activity, R.layout.songs_listview_content, objects);
        this.activity = activity;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.songs_listview_content, null);
        }
        E text = getItem(position);
        if (text != null) {
            setTextView(position, view, text);
            setImageView(view);
        }

        return view;
    }

    private void setTextView(int position, View v, E e)
    {
        TextView textView = (TextView) v.findViewById(R.id.tamil_title);
        if (textView != null) {
            AbstractDomain abstractDomain = (AbstractDomain) e;
            textView.setText(abstractDomain.getName());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(new TextOnClickListener(e));
        }
        if (getSelectedItem() == position) {
            textView.setBackgroundResource(R.color.gray);
        } else {
            textView.setBackgroundResource(R.color.white);
        }
    }

    private void setImageView(View rowView)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);
        imageView.setVisibility(View.GONE);
    }

    public void setItemSelected(int position)
    {
        selectedItem = position;
    }

    public int getSelectedItem()
    {
        return selectedItem;
    }

    private class TextOnClickListener implements View.OnClickListener
    {
        private AbstractDomain abstractDomain;

        TextOnClickListener(E e)
        {
            this.abstractDomain = (AbstractDomain) e;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(activity, SongListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CommonConstants.TYPE, type);
            intent.putExtra(CommonConstants.TITLE_KEY, abstractDomain.getName());
            intent.putExtra(CommonConstants.ID, abstractDomain.getId());
            activity.startActivity(intent);
        }
    }


}
