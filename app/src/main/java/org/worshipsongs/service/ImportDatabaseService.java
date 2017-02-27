package org.worshipsongs.service;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ImportDatabaseService
{
    void loadDb(Context context, Map<String, Object> objects);

    //void setProgressBar(ProgressBar progressBar);

    String getName();

    int getOrder();

}
