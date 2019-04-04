package org.worshipsongs.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.DragDrop;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.service.FavouriteService;

/**
 * Author: Seenivasan, Madasamy
 * version :1.0.0
 */
public class AddFavouritesDialogFragment extends DialogFragment
{
    private FavouriteService favouriteService = new FavouriteService();

    public static AddFavouritesDialogFragment newInstance(Bundle bundle)
    {
        AddFavouritesDialogFragment addFavouritesDialogFragment = new AddFavouritesDialogFragment();
        addFavouritesDialogFragment.setArguments(bundle);
        return addFavouritesDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.add_service_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        EditText serviceName = promptsView.findViewById(R.id.service_name);
        alertDialogBuilder.setTitle(R.string.favourite_title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.ok, getPositiveOnClickListener(serviceName));
        alertDialogBuilder.setNegativeButton(R.string.cancel, getNegativeOnClickListener());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.white);
        return alertDialog;
    }

    private DialogInterface.OnClickListener getNegativeOnClickListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        };
    }


    @NonNull
    private DialogInterface.OnClickListener getPositiveOnClickListener(final EditText serviceName)
    {

        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Bundle args = getArguments();
                String songName = args.getString(CommonConstants.TITLE_KEY);
                String localisedName = args.getString(CommonConstants.LOCALISED_TITLE_KEY);
                int id = args.getInt(CommonConstants.ID);
                if (serviceName.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter favourite name...!", Toast.LENGTH_LONG).show();
                } else {
                    String favouriteName = serviceName.getText().toString();
                    SongDragDrop songDragDrop = new SongDragDrop(id, songName, false);
                    songDragDrop.setTamilTitle(localisedName);
                    favouriteService.save(favouriteName, songDragDrop);
                    Toast.makeText(getActivity(), "Song added to favourite......!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        };

    }
}
