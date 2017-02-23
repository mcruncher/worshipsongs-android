package org.worshipsongs.service;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ImportDatabaseService
{
    void loadDb(Context context, Fragment fragment);

    void setProgressBar(ProgressBar progressBar);

    String getName();

    int getOrder();

}
