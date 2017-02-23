package org.worshipsongs.locator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IImportDatabaseLocator
{
    void load(Context context, Fragment fragment, int index, ProgressBar progressBar);

}
