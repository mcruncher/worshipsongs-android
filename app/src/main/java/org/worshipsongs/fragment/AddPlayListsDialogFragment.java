package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.worshipsongs.service.CommonService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/17/2015.
 */
public abstract class AddPlayListsDialogFragment extends DialogFragment {

    private CommonService commonService = new CommonService();
    private SongListAdapterService adapterService = new SongListAdapterService();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.add_service_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
        alertDialogBuilder.setTitle("Enter the playlist name:");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String service_name;
                if (serviceName.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "Enter playlist name...!", Toast.LENGTH_LONG).show();
                else {
                    service_name = serviceName.getText().toString();
                    List<String> serviceNames = new ArrayList<String>();
                    serviceNames.addAll(commonService.readServiceName());
                    //adapterService.setServiceNames(serviceNames);
                    commonService.saveIntoFile(service_name, getSelectedSong().toString());
                    Toast.makeText(getActivity(), "Song added to playlist...!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    SongsListFragment listFragment = new SongsListFragment();
                    listFragment.onRefresh();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });


        return alertDialogBuilder.create();
    }

    public abstract String getSelectedSong();

    //  protected abstract void onClick(int which);
}
