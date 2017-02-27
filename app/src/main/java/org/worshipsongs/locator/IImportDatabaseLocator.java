package org.worshipsongs.locator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IImportDatabaseLocator
{
    void load(Context context, Map<String, Object> objects);

}
