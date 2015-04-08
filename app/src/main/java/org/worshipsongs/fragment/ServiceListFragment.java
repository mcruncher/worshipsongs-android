package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.ServiceSongListActivity;
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
    String serviceName;
    TextView serviceMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity = (FragmentActivity) super.getActivity();
        linearLayout = (LinearLayout) inflater.inflate(R.layout.service_list_activity, container, false);
        serviceListView = (ListView) linearLayout.findViewById(R.id.list_view);
        serviceMsg = (TextView) linearLayout.findViewById(R.id.serviceMsg);
        loadService();
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        serviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                vibrator.vibrate(15);
                serviceName = serviceListView.getItemAtPosition(position).toString();
                System.out.println("Selected Song for Service:" + service);
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.service_delete_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
                        PropertyUtils.removeProperty(serviceName, serviceFile);
                        Toast.makeText(getActivity(), "Service " + serviceName + " Deleted...!", Toast.LENGTH_LONG).show();
                        service.clear();
                        loadService();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                serviceName = serviceListView.getItemAtPosition(position).toString();
                System.out.println("Selected Service:" + serviceName);
                Intent intent = new Intent(getActivity(), ServiceSongListActivity.class);
                intent.putExtra("serviceName", serviceName);
                startActivity(intent);
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
        if (service.size() <= 0)
            serviceMsg.setText("You haven't created any service yet!\n" +
                    "Services are a great way to organize selected songs for events.\n" +
                    "To add a song to the service, go to the Songs screen and long press a song.");
        else
            serviceMsg.setVisibility(View.GONE);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, service);
        serviceListView.setAdapter(adapter);
    }

    public List readServiceName()
    {
        Properties property = new Properties();
        InputStream inputStream = null;
        try {
            serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            inputStream = new FileInputStream(serviceFile);
            property.load(inputStream);

            Enumeration<?> e = property.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                //String value = property.getProperty(key);
                service.add(key);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return service;
    }
}
