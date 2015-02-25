package org.worshipsongs.page.component.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.domain.Service;
import org.worshipsongs.domain.Song;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Pitchu on 12/30/2014.
 */
public class ServiceListFragment extends Fragment
{
    private LinearLayout linearLayout;
    private FragmentActivity FragmentActivity;
    private ListView serviceListView;
    private File serviceFile = null;
    private ArrayAdapter<String> adapter;
    List<String> service = new ArrayList<String>();
    Button favourites;
    final Context context = getActivity();
    String serviceName;
    private ArrayAdapter<Service> adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity  = (FragmentActivity) super.getActivity();
        linearLayout = (LinearLayout) inflater.inflate(R.layout.service_list_activity, container, false);
        serviceListView = (ListView) linearLayout.findViewById(R.id.list_view);

        loadService();

        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView < ? > parent, View view, int position, long id)
            {
                System.out.println("Selected service:"+position);
                Service selectedValue = adapter1.getItem(position);
                String key = selectedValue.getKey();
                System.out.println("Selected service:"+key);
            }
        });
        return linearLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    public void loadService()
    {
        readServiceName();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, service);
        serviceListView.setAdapter(adapter);
    }

    public List readServiceName()
    {
        Properties property = new Properties();
        InputStream inputStream = null;
        int i = 0;
        try
        {
            serviceFile = PropertyUtils.getServicePropertyFile(getActivity());
            inputStream = new FileInputStream(serviceFile);
            property.load(inputStream);

            Enumeration<?> e = property.propertyNames();
            while (e.hasMoreElements())
            {
                String key = (String) e.nextElement();
                String value = property.getProperty(key);
                System.out.println("Service Key:"+key);
                System.out.println("Service Value:"+value);
                service.add(key);
            }
            inputStream.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return service;
    }

    private void saveIntoFile(String serviceName)
    {
        try {
            serviceFile = PropertyUtils.getServicePropertyFile(context);
            System.out.println("FilePath:" + serviceFile);
            if (!serviceFile.exists()) {
                FileUtils.touch(serviceFile);
            }
            PropertyUtils.setServiceProperty(serviceName, "", serviceFile);
            } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }
}