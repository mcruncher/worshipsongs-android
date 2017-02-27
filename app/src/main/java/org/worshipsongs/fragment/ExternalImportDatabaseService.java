package org.worshipsongs.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.service.ImportDatabaseService;

import java.util.Map;

import static android.R.attr.fragment;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class ExternalImportDatabaseService implements ImportDatabaseService
{
    private Map<String, Object> objects;
    private Context context;


    @Override
    public void loadDb(Context context, Map<String, Object> objects)
    {
        this.context = context;
        this.objects = objects;
        showFileChooser();
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
            Fragment fragment = (Fragment) objects.get(CommonConstants.FRAGMENT_KEY);
            fragment.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Import"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
        }
    }
}
