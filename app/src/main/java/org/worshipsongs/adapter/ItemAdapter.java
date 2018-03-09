
package org.worshipsongs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.DragDrop;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  Madasamy
 * version: 3.x.x
 */

public class ItemAdapter extends DragItemAdapter<DragDrop, ItemAdapter.ViewHolder>
{

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private Listener listener;

    public ItemAdapter(ArrayList<DragDrop> list, int layoutId, int grabHandleId, boolean dragOnLongPress)
    {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setHasStableIds(true);
        setItemList(list);
        if (listener != null) {
            listener.enableButton(isEnable(list));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).getTitle();
        int identifier = WorshipSongApplication.getContext().getResources().getIdentifier(text,
                "string", WorshipSongApplication.getContext().getPackageName());
        holder.mText.setText( WorshipSongApplication.getContext().getString(identifier));
        holder.checkbox.setChecked(mItemList.get(position).isChecked());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mItemList.get(position).setChecked(isChecked);
                if (listener != null) {
                    listener.enableButton(isEnable(mItemList));
                }
            }
        });
        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getItemId(int position)
    {
        return mItemList.get(position).getId();
    }

    class ViewHolder extends DragItemAdapter.ViewHolder
    {
        TextView mText;
        CheckBox checkbox;

        ViewHolder(final View itemView)
        {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }

    }

    private boolean isEnable(List<DragDrop> dragDrops)
    {
        for (DragDrop dragDrop : dragDrops) {
            if (dragDrop.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    public interface Listener
    {

        void enableButton(boolean enable);
    }
}
