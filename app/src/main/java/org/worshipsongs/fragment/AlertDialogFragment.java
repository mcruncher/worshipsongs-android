package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;

import java.lang.reflect.Field;


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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.DialogTheme));
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
        titleTextView.setVisibility(StringUtils.isBlank(titleTextView.getText().toString()) ? View.GONE : View.VISIBLE);

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

    //https://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception ex) {
            Log.e(AlertDialogFragment.class.getSimpleName(), "Error", ex);
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag).addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.e(AlertDialogFragment.class.getSimpleName(), "Error", e);
        }
    }

}