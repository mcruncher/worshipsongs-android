package org.worshipsongs.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.ServiceListAdapter;
import org.worshipsongs.domain.Song;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/10/2015.
 */
public class DropDownList implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> dataAdapter;
    private List<String> serviceList = new ArrayList<String>();
    private CommonService commonService = new CommonService();
    private ServiceListAdapter serviceListAdapter;
    private WorshipSongApplication application = new WorshipSongApplication();
    private AlertDialog alertDialog;
    private LayoutInflater inflater;
    private PopupWindow popupWindow;
    private Song selectedSong;
    private Context context;

    public DropDownList() {
    }

    public DropDownList(Context context, PopupWindow popupWindow, Song selectedSong) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.popupWindow = popupWindow;
        this.selectedSong = selectedSong;
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        popupWindow.dismiss();
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.service_name_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        dataAdapter = new ArrayAdapter<String>(context, R.layout.service_alertdialog_content, serviceList);

        final TextView title = (TextView) promptsView.findViewById(R.id.songTitle);
        final ListView serviceListView = (ListView) promptsView.findViewById(R.id.service_list);
        title.setText("Add to playlist");
        title.setTypeface(Typeface.DEFAULT_BOLD);
        serviceList.clear();
        serviceList.add("New playlist...");
        serviceList = commonService.readServiceName();
        dataAdapter.addAll(serviceList);
        //serviceListAdapter = new ServiceListAdapter(context, serviceList);
        //serviceListView.setAdapter(serviceListAdapter);
        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //String service = serviceListView.getItemAtPosition(position).toString();
                String service = dataAdapter.getItem(position);
                System.out.println("Selected Song for Service:" + service);
                if (position == 0) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.add_service_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptsView);
                    final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String service_name;
                            if (serviceName.getText().toString().equals(""))
                                Toast.makeText(context, "Enter playlist name...!", Toast.LENGTH_LONG).show();
                            else {
                                service_name = serviceName.getText().toString();
                                System.out.println("Service:" + service_name);
                                System.out.println("Selected Song:" + selectedSong.toString());

                                commonService.saveIntoFile(service_name, selectedSong.toString());
                                Toast.makeText(context, "Song added to playlist...!", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    commonService.saveIntoFile(service, selectedSong.toString());
                    Toast.makeText(context, "Song added to playlist...!", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }
            }
        });

        alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.show();
    }
}
