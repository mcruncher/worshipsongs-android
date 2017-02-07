package org.worshipsongs.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.worship.R;

import java.util.List;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class NewPresentSongCardViewAdapter extends ArrayAdapter<String>
{
    private final ListView listView;
    //private final List<String> objects;
    private Song song;
    private int selectedItem = -1;
    private final Context context;
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private FloatingActionButton nextButton;
    private int currentPosition;
    private FloatingActionButton previousButton;
    private FloatingActionsMenu floatingActionMenu;

    public NewPresentSongCardViewAdapter(ListView listView, Context context, Song song)
    {
        super(context, R.layout.new_present_song_adapter_layout, song.getContents());
        this.listView = listView;
        //this.objects = song.getContents();
        this.song = song;
        this.context = context;
        this.currentPosition = 0;
        preferenceSettingService = new UserPreferenceSettingService();
        customTagColorService = new CustomTagColorService();
    }

    public void setItemSelected(int position)
    {
        selectedItem = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.new_present_song_adapter_layout, null);
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
            customTagColorService.setCustomTagTextView(context, verse, textView);
            textView.setTypeface(preferenceSettingService.getFontStyle());
            textView.setTextSize(preferenceSettingService.getPortraitFontSize());
            textView.setTextColor(preferenceSettingService.getColor());
            textView.setVerticalScrollBarEnabled(true);
        }
        if (selectedItem == position) {
            textView.setBackgroundResource(R.color.gray);
        } else {
            textView.setBackgroundResource(R.color.white);
        }
    }

//    private void setFloatingActionMenu(final View view, Song song)
//    {
//        floatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_action_menu);
//        if (isPlayVideo(song.getUrlKey())) {
//            floatingActionMenu.setVisibility(View.VISIBLE);
//            floatingActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener()
//            {
//                @Override
//                public void onMenuExpanded()
//                {
//                    int color = R.color.gray_transparent;
//                    //setRecycleViewForegroundColor(ContextCompat.getColor(getActivity(), color));
//                }
//
//                @Override
//                public void onMenuCollapsed()
//                {
//                    int color = 0x00000000;
//                    //setRecycleViewForegroundColor(color);
//                }
//            });
//            setPlaySongFloatingMenuButton(view, song.getUrlKey());
//            setPresentSongFloatingMenuButton(view);
//        } else {
//            floatingActionMenu.setVisibility(View.GONE);
//            setPresentSongFloatingButton(view);
//        }
//
//    }



//    private boolean isPlayVideo(String urrlKey)
//    {
//        boolean playVideoStatus = preferenceSettingService.getPlayVideoStatus();
//        return urrlKey != null && urrlKey.length() > 0 && playVideoStatus;
//    }

//    private void setPlaySongFloatingMenuButton(View view, final String urrlKey)
//    {
//        FloatingActionButton playSongFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.play_song_floating_menu_button);
//        if (isPlayVideo(urrlKey)) {
//            playSongFloatingActionButton.setVisibility(View.VISIBLE);
//            playSongFloatingActionButton.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    // showYouTube(urrlKey);
//                    if (floatingActionMenu.isExpanded()) {
//                        floatingActionMenu.collapse();
//                    }
//                }
//            });
//        }
//    }
//
//    private void setPresentSongFloatingMenuButton(View view)
//    {
//        final FloatingActionButton presentSongFloatingMenuButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_menu_button);
//        presentSongFloatingMenuButton.setVisibility(View.VISIBLE);
//        presentSongFloatingMenuButton.setOnClickListener(new View.OnClickListener()
//        {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onClick(View view)
//            {
//                //startPresentActivity();
//                currentPosition = 0;
//                listView.smoothScrollToPosition(0);
//                notifyDataSetChanged();
//                if (floatingActionMenu.isExpanded()) {
//                    floatingActionMenu.collapse();
//                }
//                floatingActionMenu.setVisibility(View.GONE);
//
//            }
//        });
//    }

//    private void setPresentSongFloatingButton(View view)
//    {
//        final FloatingActionButton presentSongFloatingButton = (FloatingActionButton) view.findViewById(R.id.present_song_floating_button);
//        presentSongFloatingButton.setVisibility(View.VISIBLE);
//        presentSongFloatingButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                currentPosition = 0;
//                listView.smoothScrollToPosition(0);
//                presentSongFloatingButton.setVisibility(View.GONE);
//                //startPresentActivity
//            }
//        });
//    }
//
//    private void setNextButton(View view)
//    {
//        nextButton = (FloatingActionButton) view.findViewById(R.id.next_verse_floating_button);
//        nextButton.setVisibility(View.VISIBLE);
//        nextButton.setOnClickListener(new NextButtonOnClickListener());
//    }
//
//    private void setPreviousButton(View view)
//    {
//        previousButton = (FloatingActionButton) view.findViewById(R.id.previous_verse_floating_button);
//        previousButton.setOnClickListener(new PreviousButtonOnClickListener());
//    }
//
//    private class NextButtonOnClickListener implements View.OnClickListener
//    {
//
//
//        @Override
//        public void onClick(View v)
//        {
//            currentPosition = currentPosition + 1;
//            if (song.getContents().size() == currentPosition) {
//                nextButton.setVisibility(View.GONE);
//            }
//            if (song.getContents().size() > currentPosition) {
//                //showNextVerse(currentPosition);
//                listView.smoothScrollToPositionFromTop(currentPosition, 2);
//                previousButton.setVisibility(View.VISIBLE);
//                setItemSelected(currentPosition);
//                notifyDataSetChanged();
////                presentSongCardViewAdapter.notifyDataSetChanged();
//            }
//        }
//    }
//
//    private class PreviousButtonOnClickListener implements View.OnClickListener
//    {
//        @Override
//        public void onClick(View v)
//        {
//            currentPosition = currentPosition - 1;
//            if (currentPosition == song.getContents().size()) {
//                currentPosition = currentPosition - 1;
//            }
//            if (currentPosition <= song.getContents().size() && currentPosition >= 0) {
//                Log.i(PresentSongActivity.class.getSimpleName(), "Current position after dec: " + currentPosition);
//                //showNextVerse(currentPosition);
//                listView.smoothScrollToPosition(currentPosition, 2);
//                nextButton.setVisibility(View.VISIBLE);
//                setItemSelected(currentPosition);
//                setItemSelected(currentPosition);
//                notifyDataSetChanged();
//                setItemSelected(currentPosition);
//                notifyDataSetChanged();
//            }
//            if (currentPosition == 0) {
//                previousButton.setVisibility(View.GONE);
//            }
//        }
//    }
}
