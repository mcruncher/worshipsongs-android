package org.worshipsongs.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.ServiceSongListActivity;
import org.worshipsongs.utils.CommonUtils;
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
 * author  :Pitchumani, madasamy
 * version: 1.0.0
 */
public class ServiceListFragment extends Fragment
{
    List<String> serviceNames = new ArrayList<String>();
    String serviceName;
    TextView serviceMsg;
    ListAdapter listAdapter;
    private LinearLayout linearLayout;
    private FragmentActivity FragmentActivity;
    private ListView serviceListView;
    private File serviceFile = null;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity = (FragmentActivity) super.getActivity();
        linearLayout = (LinearLayout) inflater.inflate(R.layout.service_list_activity, container, false);
        serviceListView = (ListView) linearLayout.findViewById(R.id.list_view);
        serviceMsg = (TextView) linearLayout.findViewById(R.id.serviceMsg);
        serviceNames.clear();
        loadService();
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        serviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                vibrator.vibrate(15);
                //serviceName = serviceListView.getItemAtPosition(position).toString();
                serviceName = adapter.getItem(position).toString();
                System.out.println("Selected Song for Service:" + serviceName);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptsView = layoutInflater.inflate(R.layout.delete_confirmation_dialog, null);
                TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
                deleteMsg.setText(R.string.message_delete_playlist);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
                        PropertyUtils.removeProperty(serviceName, serviceFile);
                        Toast.makeText(getActivity(), "Favourite " + serviceName + " deleted...!", Toast.LENGTH_SHORT).show();
                        serviceNames.clear();
                        loadService();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {
                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(getResources().getColor(R.color.accent_material_light));
                    }
                });
                alertDialog.show();
                return true;
            }
        });

        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                serviceName = adapter.getItem(position).toString();
                //serviceName = serviceListView.getItemAtPosition(position).toString();
                System.out.println("Selected Service:" + serviceName);
                Intent intent = new Intent(getActivity(), ServiceSongListActivity.class);
                intent.putExtra("serviceName", serviceName);
                startActivity(intent);
            }
        });
        return linearLayout;
    }

    public void loadService()
    {
        serviceNames.clear();
        readServiceName();
        if (serviceNames.size() <= 0) {
            serviceMsg.setText("You haven't created any Favourite yet!\n" +
                    "Favourites are a great way to organize selected songs for events.\n" +
                    "To add a song to a Favourites, tap the : icon near a song and select the " + "Add to Favourite" + " action.");
        } else {
            serviceMsg.setVisibility(View.GONE);
        }
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, serviceNames);
        listAdapter = new ListAdapter(getActivity());
        serviceListView.setAdapter(listAdapter);
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
                serviceNames.add(key);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return serviceNames;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                adapter.getFilter().filter(query);
                return true;

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(this.getClass().getSimpleName(), "Is visible to user ?" + isVisibleToUser);
        if (isVisibleToUser && getView() != null) {
            loadService();
            CommonUtils.hideKeyboard(getActivity());
        }
    }

    private class ListAdapter extends BaseAdapter
    {
        LayoutInflater inflater;

        public ListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = inflater.inflate(R.layout.service_listview_content, null);
            TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);
            Button delete = (Button) convertView.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
            serviceName.setText(serviceNames.get(position).trim());
            return convertView;
        }

        public int getCount()
        {
            return serviceNames.size();
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }
    }
}