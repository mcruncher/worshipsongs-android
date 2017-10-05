package org.worshipsongs.registry;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.worshipsongs.R;
import org.worshipsongs.listener.SongContentViewListener;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class DummyFragment1 extends Fragment implements ITabFragment
{
    @Override
    public int defaultSortOrder()
    {
        return 1;
    }

    @Override
    public int getTitle()
    {
        return R.string.title;
    }

    @Override
    public boolean checked()
    {
        return true;
    }

    @Override
    public void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle)
    {

    }
}
