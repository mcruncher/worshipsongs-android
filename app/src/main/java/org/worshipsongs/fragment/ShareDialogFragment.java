package org.worshipsongs.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.worship.R;

/**
 * Author: Madasamy
 * Version: 2.5.0
 */
public abstract class ShareDialogFragment extends DialogFragment
{

    private WorshipSongApplication application = new WorshipSongApplication();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.share_dialog, null);
        setWhatsappImageView(promptsView);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("");
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
        return alertDialog;
    }

    private void setWhatsappImageView(View promptsView)
    {
        ImageView whatsappImageView = (ImageView) promptsView.findViewById(R.id.whatsapp_imageView);
        whatsappImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getContent());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.getContext().startActivity(sendIntent);
            }
        });
    }

    protected abstract String getContent();
}

