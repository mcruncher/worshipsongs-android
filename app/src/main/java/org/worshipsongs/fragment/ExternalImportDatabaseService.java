package org.worshipsongs.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
    private AppCompatActivity appCompatActivity;


    @Override
    public void loadDb(AppCompatActivity appCompatActivity, Map<String, Object> objects)
    {
        this.appCompatActivity = appCompatActivity;
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
        try {
            appCompatActivity.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Import"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }
}
