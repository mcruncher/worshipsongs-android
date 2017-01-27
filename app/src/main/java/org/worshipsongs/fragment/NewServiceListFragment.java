package org.worshipsongs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.worshipsongs.adapter.DragNDropListView;
import org.worshipsongs.adapter.DragNDropSimpleAdapter;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 2.x
 */

public class NewServiceListFragment extends Fragment
{
    public static NewServiceListFragment newInstance()
    {
        return new NewServiceListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 30; ++i) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", "item" + i);
            item.put("_id", i);

            items.add(item);
        }
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.service_list_layout, container, false);
        DragNDropListView dragNDropListView = (DragNDropListView) linearLayout.findViewById(android.R.id.list);
        DragNDropSimpleAdapter adapter = new DragNDropSimpleAdapter(getActivity(),
                items, R.layout.row_layout, new String[]{"name"}, new int[]{R.id.text}, R.id.handler);
        dragNDropListView.setDragNDropAdapter(adapter);
        return linearLayout;
    }
}
