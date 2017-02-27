package org.worshipsongs.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import org.worshipsongs.service.ImportDatabaseService;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class ExternalImportDatabaseService implements ImportDatabaseService
{
    private ProgressBar progressBar;
    private Context context;
    private Fragment fragment;

    @Override
    public void loadDb(Context context, Fragment fragment)
    {
        this.context = context;
        this.fragment = fragment;
        showFileChooser();
    }

    @Override
    public void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }

    @Override
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder()
    {
        return 1;
    }

    private void showFileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        intent.setDataAndType(uri, "*/*");
        //startActivity(Intent.createChooser(intent, "Open folder"));
//        Intent intent = new Intent(Intent.ACTION_CHOOSER);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            fragment.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Import"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
        }
    }
}
