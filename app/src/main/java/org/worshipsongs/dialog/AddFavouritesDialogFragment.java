package org.worshipsongs.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.service.CommonService;
import org.worshipsongs.R;

/**
 * Author: Seenivasan, Madasamy
 * version :1.0.0
 */
public class AddFavouritesDialogFragment extends DialogFragment
{
    private CommonService commonService = new CommonService();

    public static AddFavouritesDialogFragment newInstance(String songName)
    {
        AddFavouritesDialogFragment addFavouritesDialogFragment = new AddFavouritesDialogFragment();
        Bundle bundleArgs = new Bundle();
        bundleArgs.putString(CommonConstants.TITLE_KEY, songName);
        addFavouritesDialogFragment.setArguments(bundleArgs);
        return addFavouritesDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.add_service_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
        alertDialogBuilder.setTitle(R.string.favourite_title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.ok, getPositiveOnClickListener(serviceName));
        alertDialogBuilder.setNegativeButton(R.string.cancel, getNegativeOnClickListener());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return alertDialog;
    }

    @NonNull
    private DialogInterface.OnClickListener getPositiveOnClickListener(final EditText serviceName)
    {
        return new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Bundle args = getArguments();
                String songName = args.getString(CommonConstants.TITLE_KEY);
                String service_name;
                if (serviceName.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter favourite name...!", Toast.LENGTH_LONG).show();
                } else {
                    service_name = serviceName.getText().toString();
                    commonService.saveIntoFile(service_name, songName);
                    Toast.makeText(getActivity(), "Song added to favourite...!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        };
    }

    @NonNull
    private DialogInterface.OnClickListener getNegativeOnClickListener()
    {
        return new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
    }


}
