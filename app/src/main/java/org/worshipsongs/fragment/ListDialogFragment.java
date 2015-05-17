package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Author: Arunachalam
 * Since: 1.0.0
 */
public abstract class ListDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add to play lists")
                .setItems(getProductListsArray(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ListDialogFragment.this.onClick(which);
                    }
                });
        return builder.create();
    }

    public abstract String[] getProductListsArray();

    protected abstract void onClick(int which);
}
