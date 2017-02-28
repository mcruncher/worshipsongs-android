package org.worshipsongs.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.worshipsongs.activity.DatabaseSettingActivity;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : 3.x.
 */

public class DatabaseSettingsPreference extends Preference
{

    public DatabaseSettingsPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        getContext().startActivity(new Intent(getContext(), DatabaseSettingActivity.class));
    }


    //    @Override
//    protected View onCreateView(ViewGroup parent)
//    {
//        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.database_layout, parent, false);
//        setImportDatabaseButton(view);
//        return view;
//    }
//
//    private void setImportDatabaseButton(View view)
//    {
//        Button importDatabaseButton = (Button) view.findViewById(R.id.upload_database_button);
//        importDatabaseButton.setOnClickListener(new ImportDatabaseOnClickListener());
//    }
//
//    private class ImportDatabaseOnClickListener implements View.OnClickListener
//    {
//        @Override
//        public void onClick(View view)
//        {
//            //showDatabaseTypeDialog();
//        }
//    }
//
////    private void showDatabaseTypeDialog()
////    {
////        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
////        builder.setTitle(getActivity().getString(R.string.type));
////        builder.setItems(R.array.dataBaseTypes, new DialogInterface.OnClickListener()
////        {
////            @Override
////            public void onClick(DialogInterface dialog, int which)
////            {
////                importDatabaseLocator.load(getActivity(), getStringObjectMap(which));
////                dialog.cancel();
////            }
////        });
////        AlertDialog dialog = builder.create();
////        dialog.getListView().setSelector(android.R.color.darker_gray);
////        dialog.show();
////    }


}
