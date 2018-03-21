package org.worshipsongs.registry;

import android.os.Bundle;

import org.worshipsongs.listener.SongContentViewListener;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public interface ITabFragment
{
    int defaultSortOrder();

    String getTitle();

    boolean checked();

    void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle);
}
