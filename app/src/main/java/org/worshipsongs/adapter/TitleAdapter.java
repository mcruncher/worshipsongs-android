package org.worshipsongs.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class TitleAdapter<T> extends ArrayAdapter<T>
{

    private TitleAdapterListener<T> titleAdapterListener;

    public TitleAdapter(@NonNull AppCompatActivity context, @LayoutRes int resource)
    {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.title_row, null);
        }
        setViews(view, position);
        return view;
    }

    private void setViews(View view, int position)
    {
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put(CommonConstants.TITLE_KEY, getTitlesView(view));
        maps.put(CommonConstants.SUBTITLE_KEY, getSubtitleTextView(view));
        maps.put(CommonConstants.COUNT_KEY, getCountView(view));
        maps.put(CommonConstants.PLAY_IMAGE_KEy, getPlayImageView(view));
        maps.put(CommonConstants.OPTIONS_IMAGE_KEY, getOptionsImageView(view));
        maps.put(CommonConstants.POSITION_KEY, position);
        titleAdapterListener.setViews(maps, getItem(position));
    }

    private TextView getTitlesView(View view)
    {
        return (TextView) view.findViewById(R.id.title_text_view);
    }

    private TextView getCountView(View view) {
        return (TextView)view.findViewById(R.id.count_text_view);
    }

    private TextView getSubtitleTextView(View view)
    {
        return (TextView) view.findViewById(R.id.subtitle_text_view);
    }

    private ImageView getPlayImageView(final View rowView)
    {
        return (ImageView) rowView.findViewById(R.id.video_image_view);
    }

    private ImageView getOptionsImageView(View rowView)
    {
        return (ImageView) rowView.findViewById(R.id.option_image_view);
    }

    public void addObjects(List<T> objects)
    {
        clear();
        addAll(objects);
        notifyDataSetChanged();
    }

    public void setTitleAdapterListener(TitleAdapterListener titleAdapterListener)
    {
        this.titleAdapterListener = titleAdapterListener;
    }

    public interface TitleAdapterListener<T>
    {
        void setViews(Map<String, Object> objects, T t);
    }
}
