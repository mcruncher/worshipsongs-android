package org.worshipsongs.page.component.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity  = (FragmentActivity) super.getActivity();
        linearLayout = (LinearLayout) inflater.inflate(R.layout.service_list_activity, container, false);
        serviceListView = (ListView) linearLayout.findViewById(R.id.list_view);

        loadService();

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
        menu.findItem(R.id.action_service).setVisible(false);
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
                //String value = prop.getProperty(key);
                service.add(key);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return service;
    }

    public void loadService()
    {
        readServiceName();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, service);
        serviceListView.setAdapter(adapter);
    }
}