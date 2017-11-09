package org.worshipsongs.preference;

import android.app.Activity;
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

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.adapter.ItemAdapter;
import org.worshipsongs.domain.DragDrop;
import org.worshipsongs.registry.FragmentRegistry;

import java.util.ArrayList;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class TabChoicePreference extends DialogPreference implements ItemAdapter.Listener
{
    private ArrayList<DragDrop> configureDragDrops;
    private DragListView mDragListView;
    private SharedPreferences sharedPreferences;
    private FragmentRegistry fragmentRegistry = new FragmentRegistry();

    public TabChoicePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
            sharedPreferences.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(itemList)).apply();
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply();
        }
        super.onDialogClosed(positiveResult);
    }

    private void setItems()
    {
        configureDragDrops = DragDrop.toArrays(sharedPreferences.getString(CommonConstants.TAB_CHOICE_KEY, ""));
        ArrayList<DragDrop> defaultList = fragmentRegistry.getDragDrops((Activity) getContext());
        if (configureDragDrops == null || configureDragDrops.isEmpty()) {
            configureDragDrops = defaultList;
            sharedPreferences.edit().putString(CommonConstants.TAB_CHOICE_KEY, DragDrop.toJson(configureDragDrops)).apply();
        } else if (defaultList.size() > configureDragDrops.size()) {
            defaultList.removeAll(configureDragDrops);
            configureDragDrops.addAll(defaultList);
        }
    }

    private void setDragListView(View view)
    {
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter listAdapter = new ItemAdapter(configureDragDrops, R.layout.tab_choice_item, R.id.image, false);
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
