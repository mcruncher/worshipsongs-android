package org.worshipsongs.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.worshipsongs.service.CommonService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Seenivasan, Madasamy
 * version :1.0.0
 */

public class ListDialogFragment extends DialogFragment
{
    private CommonService commonService = new CommonService();

    public static ListDialogFragment newInstance(String songName) {
        ListDialogFragment listDialogFragment = new ListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("songName", songName);
        listDialogFragment.setArguments(bundle);
        return listDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.addToPlayList))
                .setItems(getProductListsArray(), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ListDialogFragment.this.onClick(which);
                    }
                });

        return builder.create();
    }

    private  String[] getProductListsArray(){
        List<String> services = new ArrayList<String>();
        services.addAll(commonService.readServiceName());
        services.add(0, "New favourite...");
        Log.d("service names list", commonService.readServiceName().toString());
        String[]  serviceNames = new String[services.size()];
        services.toArray(serviceNames);
        Log.d("service names are", services.toString());
        return serviceNames;
    }

    private  void onClick(int which){
        Bundle args = getArguments();
        String songName = args.getString("songName");
        if (which == 0) {
            AddPlayListsDialogFragment addPlayListsDialogFragment = AddPlayListsDialogFragment.newInstance(songName);
            addPlayListsDialogFragment.show(getActivity().getSupportFragmentManager(), "AddPlayListDialog");
        } else {
            List<String> services = new ArrayList<String>();
            services.addAll(commonService.readServiceName());
            String[] serviceNames = new String[services.size()];
            String[] selectedServiceNames = services.toArray(serviceNames);
            commonService.saveIntoFile(selectedServiceNames[which - 1].toString(), songName);
            Toast.makeText(getActivity(), "Song added to favourite...!", Toast.LENGTH_LONG).show();
        }
    }


}
