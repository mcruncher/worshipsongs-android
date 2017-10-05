package org.worshipsongs.registry;

import android.os.Bundle;
import android.support.annotation.StringRes;

import org.worshipsongs.listener.SongContentViewListener;

/**
 * Author : Madasamy
 * Version : 4.x.x
 */

public interface ITabFragment
{
    int defaultSortOrder();

    @StringRes
    int getTitle();

    boolean checked();

    void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle);
}
