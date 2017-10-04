package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.adapter.ItemAdapter;
import org.worshipsongs.domain.DragDrop;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class TabChoicePreference extends DialogPreference implements ItemAdapter.Listener
{
    private ArrayList<DragDrop> mItemArray;
    private DragListView mDragListView;
    private SharedPreferences preferences;

    public TabChoicePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        setItems();
        setDialogLayoutResource(R.layout.tab_choice_layout);
    }

    @Override
    protected void onBindDialogView(View view)
    {
        setDragListView(view);
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        Log.i(TabChoicePreference.class.getSimpleName(), "Tab choice preference " + positiveResult);
        if (positiveResult) {
            ArrayList<DragDrop> itemList = (ArrayList<DragDrop>) mDragListView.getAdapter().getItemList();
            preferences.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(itemList)).apply();
        }
        super.onDialogClosed(positiveResult);
    }

    private void setItems()
    {
        mItemArray = DragDrop.toArrays(preferences.getString(CommonConstants.TAB_CHOICE_KEY, ""));
        if (mItemArray == null || mItemArray.isEmpty()) {
            mItemArray = getDefaultList();
            preferences.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(mItemArray)).apply();
        }
    }

    private ArrayList<DragDrop> getDefaultList()
    {
        ArrayList<DragDrop> list = new ArrayList<>();
        list.add(new DragDrop(0, getContext().getString(R.string.titles), true));
        list.add(new DragDrop(1, getContext().getString(R.string.artists), true));
        list.add(new DragDrop(2, getContext().getString(R.string.categories), true));
        list.add(new DragDrop(3, getContext().getString(R.string.song_books), true));
        list.add(new DragDrop(4, getContext().getString(R.string.playlists), true));
        return list;
    }

    private void save(Context context, ArrayList<DragDrop> items)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("tag", json);
        editor.apply();
    }

    private ArrayList<DragDrop> getList(Context context)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("tag", null);
        Type type = new TypeToken<ArrayList<DragDrop>>()
        {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void setDragListView(View view)
    {
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.tab_choice_item, R.id.image, false);
        listAdapter.setListener(this);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.tab_choice_item));
    }

    @Override
    public void enableButton(boolean enable)
    {
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enable);
    }

    private static class MyDragItem extends DragItem
    {
        MyDragItem(Context context, int layoutId)
        {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView)
        {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.findViewById(R.id.item_layout).setBackgroundColor(dragView.getResources().getColor(R.color.list_item_background));
        }
    }

}
