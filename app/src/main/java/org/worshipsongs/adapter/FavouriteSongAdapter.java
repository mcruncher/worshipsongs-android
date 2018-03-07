package org.worshipsongs.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.swipe.ListSwipeItem;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.DragDrop;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.service.UserPreferenceSettingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class FavouriteSongAdapter extends DragItemAdapter<SongDragDrop, FavouriteSongAdapter.ViewHolder>
{
    private UserPreferenceSettingService userPreferenceSettingService = new UserPreferenceSettingService();
    private FavouriteListener favouriteListener;

    public FavouriteSongAdapter(List<SongDragDrop> songs)
    {
        setHasStableIds(true);
        setItemList(songs);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_song_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        super.onBindViewHolder(holder, position);
        String text = getTitle(mItemList.get(position));
        holder.mText.setText(text);
        holder.itemView.setTag(mItemList.get(position));
    }

    private String getTitle(SongDragDrop songDragDrop)
    {
        if (userPreferenceSettingService.isTamil()) {
            return StringUtils.isNotBlank(songDragDrop.getTamilTitle()) ? songDragDrop.getTamilTitle() : songDragDrop.getTitle();
        } else {
            return songDragDrop.getTitle();
        }
    }

    @Override
    public long getItemId(int position)
    {
        return mItemList.get(position).getId();
    }

    public void setFavouriteListener(FavouriteListener favouriteListener)
    {
        this.favouriteListener = favouriteListener;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder
    {
        TextView mText;
        RelativeLayout listSwipeItem;

        ViewHolder(final View view)
        {
            super(view, R.id.image, false);
            mText = (TextView) itemView.findViewById(R.id.text);
            listSwipeItem = (RelativeLayout) itemView.findViewById(R.id.item_layout);
        }

        @Override
        public boolean onItemLongClicked(View view)
        {
            if (favouriteListener != null) {
                favouriteListener.onRemove(mItemList.get(getAdapterPosition()));
            }
            return true;
        }

        @Override
        public void onItemClicked(View view)
        {
            if (favouriteListener != null) {
                favouriteListener.onClick(mItemList.get(getAdapterPosition()));
            }
        }
    }

    public interface FavouriteListener
    {
        void onRemove(SongDragDrop dragDrop);

        void onClick(SongDragDrop dragDrop);
    }
}
