package org.worshipsongs.registry;

import android.os.Bundle;


import org.worshipsongs.listener.SongContentViewListener;

import androidx.fragment.app.Fragment;

/**
 * @author  Madasamy
 * @since 3.x.x
 */

public class DummyFragment1 extends Fragment implements ITabFragment
{
    @Override
    public int defaultSortOrder()
    {
        return 1;
    }

    @Override
    public String getTitle()
    {
        return "title";
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
