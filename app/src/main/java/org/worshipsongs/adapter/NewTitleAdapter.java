package org.worshipsongs.adapter;

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
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class NewTitleAdapter extends ArrayAdapter<Song>
{

    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private SongListAdapterService songListAdapterService = new SongListAdapterService();
    private TitleAdapterListener titleAdapterListener;
    private AppCompatActivity activity;

    public NewTitleAdapter(@NonNull AppCompatActivity context, @LayoutRes int resource)
    {
        super(context, resource);
        this.activity = context;
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
        Song song = getItem(position);
        setTitleTextView(view, song, position);
        setPlayImageView(view, song, activity.getSupportFragmentManager());
        setImageView(view, song.getTitle(), activity.getSupportFragmentManager());
        return view;
    }

    private void setTitleTextView(View view, Song song, int position)
    {
        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(getTitle(song));
        titleTextView.setOnClickListener(getTitleOnClickListener(song, position));
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(song.getTitle())) {
            titleTextView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        }
    }

    private String getTitle(Song song)
    {
        try {
            return (preferenceSettingService.isTamil() && song.getTamilTitle().length() > 0) ?
                    song.getTamilTitle() : song.getTitle();
        } catch (Exception e) {
            return song.getTitle();
        }
    }

    @NonNull
    private View.OnClickListener getTitleOnClickListener(final Song song, final int position)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (titleAdapterListener != null) {
                    titleAdapterListener.setSelectedSong(song, position);
                }
            }
        };
    }

    private void setPlayImageView(final View rowView, final Song song, FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.video_image_view);
        final String urlKey = song.getUrlKey();
        if (urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo()) {
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(onClickPopupListener(song.getTitle(), fragmentManager));
    }

    private void setImageView(View rowView, final String songTitle, final FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.option_image_view);
        imageView.setOnClickListener(onClickPopupListener(songTitle, fragmentManager));
    }

    private View.OnClickListener onClickPopupListener(final String title, final FragmentManager fragmentManager)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                songListAdapterService.showPopupmenu(view, title, fragmentManager, true);
            }
        };
    }


    public void addSongs(List<Song> songs)
    {
        clear();
        addAll(songs);
        notifyDataSetChanged();
    }

    public void setTitleAdapterListener(TitleAdapterListener titleAdapterListener)
    {
        this.titleAdapterListener = titleAdapterListener;
    }

    public interface TitleAdapterListener
    {
        void setSelectedSong(Song song, int position);
    }
}
