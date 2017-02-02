package org.worshipsongs.adapter;

import android.app.Presentation;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class PresentSongCardViewAdapter extends RecyclerView.Adapter<PresentSongCardViewAdapter.SongContentViewHolder>
{
    private final SparseArray<Presentation> activePresentations = new SparseArray<Presentation>();
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private Song song;
    private Context context;
    private int focusedItem = 0;

    public PresentSongCardViewAdapter(Song song, Context context)
    {
        this.context = context;
        this.song = song;
    }

    @Override
    public int getItemCount()
    {
        return song.getContents().size();
    }

//    @Override
//    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//
//        // Handle key up and key down and attempt to move selection
//        recyclerView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
//
//                // Return false if scrolled to the bounds and allow focus to move off the list
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//                        return tryMoveSelection(lm, 1);
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//                        return tryMoveSelection(lm, -1);
//                    }
//                }
//
//                return false;
//            }
//        });
//    }
//
//    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
//        int tryFocusItem = focusedItem + direction;
//
//        // If still within valid bounds, move the selection, notify to redraw, and scroll
//        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
//            notifyItemChanged(focusedItem);
//            focusedItem = tryFocusItem;
//            notifyItemChanged(focusedItem);
//            lm.scrollToPosition(focusedItem);
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onBindViewHolder(PresentSongCardViewAdapter.SongContentViewHolder songContentViewHolder, int position)
    {
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        String verse = song.getContents().get(position);
        songContentViewHolder.textView.setText(verse);
        loadTextStyle(songContentViewHolder.textView, position);
        songContentViewHolder.itemView.setSelected(focusedItem == position);
    }

    private void loadTextStyle(TextView textView, int position)
    {
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(context, text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getPortraitFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    @Override
    public PresentSongCardViewAdapter.SongContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.present_song_card_view, viewGroup, false);
        return new PresentSongCardViewAdapter.SongContentViewHolder(itemView);
    }

    class SongContentViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textView;

        SongContentViewHolder(View view)
        {
            super(view);
            cardView = (CardView) view.findViewById(R.id.verse_card_view);
            textView = (TextView) view.findViewById(R.id.verse_text_view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Redraw the old selection and the new
//                    notifyItemChanged(focusedItem);
//                    focusedItem = getLayoutPosition();
//                    notifyItemChanged(focusedItem);
//                }
//            });
        }


    }

}

