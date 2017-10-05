package org.worshipsongs.registry;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.worshipsongs.R;
import org.worshipsongs.listener.SongContentViewListener;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class DummyFragment2 extends Fragment implements ITabFragment
{
    @Override
    public int defaultSortOrder()
    {
        return 2;
    }

    @Override
    public int getTitle()
    {
        return R.string.song_books;
    }

    @Override
    public boolean checked()
    {
        return false;
    }

    @Override
    public void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle)
    {

    }
}
