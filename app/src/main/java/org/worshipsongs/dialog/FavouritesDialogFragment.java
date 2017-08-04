package org.worshipsongs.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Seenivasan, Madasamy
 * version :1.0.0
 */

public class FavouritesDialogFragment extends DialogFragment
{
    private CommonService commonService = new CommonService();

    public static FavouritesDialogFragment newInstance(String songName)
    {
        FavouritesDialogFragment favouritesDialogFragment = new FavouritesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CommonConstants.TITLE_KEY, songName);
        favouritesDialogFragment.setArguments(bundle);
        return favouritesDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        builder.setTitle(getString(R.string.addToPlayList))
                .setItems(getProductListsArray(), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        FavouritesDialogFragment.this.onClick(which);
                    }
                });

        return builder.create();
    }

    private String[] getProductListsArray()
    {
        List<String> services = new ArrayList<String>();
        services.addAll(commonService.readServiceName());
        services.add(0, "New favourite...");
        Log.d("service names list", commonService.readServiceName().toString());
        String[] serviceNames = new String[services.size()];
        services.toArray(serviceNames);
        Log.d("service names are", services.toString());
        return serviceNames;
    }

    private void onClick(int which)
    {
        Bundle args = getArguments();
        String songName = args.getString(CommonConstants.TITLE_KEY);
        if (which == 0) {
            AddFavouritesDialogFragment addFavouritesDialogFragment = AddFavouritesDialogFragment.newInstance(songName);
            addFavouritesDialogFragment.show(getActivity().getSupportFragmentManager(), AddFavouritesDialogFragment.class.getSimpleName());
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
