package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;


/**
 * @author Madasamy
 * @version 3.x
 */

public class AlertDialogFragment extends DialogFragment
{
    private DialogListener dialogListener;
    private boolean visiblePositiveButton = true;
    private boolean visibleNegativeButton = true;

    public static AlertDialogFragment newInstance(Bundle bundle)
    {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        alertDialogBuilder.setCustomTitle(getCustomTitleVIew());
        if (visibleNegativeButton) {
            alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                    if (dialogListener != null) {
                        dialogListener.onClickNegativeButton();
                    }
                }
            });
        }
        if (visiblePositiveButton) {
            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                    if (dialogListener != null) {
                        dialogListener.onClickPositiveButton(getArguments(), getTag());
                    }
                }
            });
        }
        return alertDialogBuilder.create();

    }

    @NonNull
    private View getCustomTitleVIew()
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = inflater.inflate(R.layout.dialog_custom_title, null);
        TextView titleTextView = (TextView) titleView.findViewById(R.id.title);
        titleTextView.setText(getArguments().getString(CommonConstants.TITLE_KEY));

        TextView messageTextView = (TextView) titleView.findViewById(R.id.subtitle);
        messageTextView.setTextColor(getActivity().getResources().getColor(R.color.black_semi_transparent));
        messageTextView.setText(getArguments().getString(CommonConstants.MESSAGE_KEY));
        return titleView;
    }


    public interface DialogListener
    {
        void onClickPositiveButton(Bundle bundle, String tag);

        void onClickNegativeButton();
    }

    public void setDialogListener(DialogListener dialogListener)
    {
        this.dialogListener = dialogListener;
    }

    public void setVisiblePositiveButton(boolean visiblePositiveButton)
    {
        this.visiblePositiveButton = visiblePositiveButton;
    }

    public void setVisibleNegativeButton(boolean visibleNegativeButton)
    {
        this.visibleNegativeButton = visibleNegativeButton;
    }
}