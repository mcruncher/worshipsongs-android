package org.worshipsongs.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.service.FavouriteService;

import java.util.List;

/**
 * Author: Seenivasan, Madasamy
 * version :1.0.0
 */

public class FavouritesDialogFragment extends DialogFragment
{
    private FavouriteService favouriteService = new FavouriteService();

    public static FavouritesDialogFragment newInstance(Bundle bundle)
    {
        FavouritesDialogFragment favouritesDialogFragment = new FavouritesDialogFragment();
        favouritesDialogFragment.setArguments(bundle);
        return favouritesDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        List<String> names = favouriteService.findNames();
        names.add(0, "New favourite...");
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        builder.setTitle(getString(R.string.addToPlayList));
        builder.setItems(names.toArray(new String[names.size()]), (dialog, which) -> {
            FavouritesDialogFragment.this.onClick(which, names);
        });
        return builder.create();
    }

    private void onClick(int which, List<String> names)
    {
        Bundle args = getArguments();
        String songName = args.getString(CommonConstants.TITLE_KEY);
        String localisedName = args.getString(CommonConstants.LOCALISED_TITLE_KEY);
        if (which == 0) {
            AddFavouritesDialogFragment addFavouritesDialogFragment = AddFavouritesDialogFragment.newInstance(args);
            addFavouritesDialogFragment.show(getActivity().getSupportFragmentManager(), AddFavouritesDialogFragment.class.getSimpleName());
        } else {
            SongDragDrop songDragDrop = new SongDragDrop(0, songName,false);
            songDragDrop.setTamilTitle(localisedName);
            favouriteService.save(names.get(which), songDragDrop);
            Toast.makeText(getActivity(), "Song added to favourite...!", Toast.LENGTH_LONG).show();
        }
    }

}
