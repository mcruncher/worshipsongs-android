package org.worshipsongs.adapter;

import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Type;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class NewTitleAdapter<T> extends ArrayAdapter<T>
{

    private TitleAdapterListener<T> titleAdapterListener;

    public NewTitleAdapter(@NonNull AppCompatActivity context, @LayoutRes int resource)
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
            view = layoutInflater.inflate(R.layout.new_title_row, null);
        }
        setTitleTextView(view, position);
        setPlayImageView(view, position);
        setImageView(view, position);
        return view;
    }

    private void setTitleTextView(View view, int position)
    {
        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleAdapterListener.setTitleTextView(titleTextView, getItem(position));
    }

    private void setPlayImageView(final View rowView, int position)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.video_image_view);
        titleAdapterListener.setPlayImageView(imageView, getItem(position), position);
    }

    private void setImageView(View rowView, int position)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.option_image_view);
        titleAdapterListener.setOptionsImageView(imageView, getItem(position), position);
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

        void setTitleTextView(TextView textView, T t);

        void setPlayImageView(ImageView imageView, T t, int position);

        void setOptionsImageView(ImageView imageView, T t, int position);

    }
}
