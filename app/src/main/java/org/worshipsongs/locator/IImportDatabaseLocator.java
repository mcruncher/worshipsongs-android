package org.worshipsongs.locator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IImportDatabaseLocator
{
    void load(AppCompatActivity appCompatActivity, Map<String, Object> objects);

}
