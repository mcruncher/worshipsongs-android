package org.worshipsongs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/10/2015.
 */
public class ServiceListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> serviceList = new ArrayList<String>();

    public ServiceListAdapter(Context context, List<String> serviceList) {
        this.inflater = LayoutInflater.from(context);
        this.serviceList = serviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.service_alertdialog_content, null);
        TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);
        ImageView serviceIcon = (ImageView) convertView.findViewById(R.id.serviceIcon);
        if (position == 0)
            serviceIcon.setImageResource(R.drawable.file);
        serviceName.setText(serviceList.get(position).toString().trim());
        return convertView;
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
