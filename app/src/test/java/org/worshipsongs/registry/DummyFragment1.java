package org.worshipsongs.registry;

import android.os.Bundle;
import androidx.core.app.Fragment;

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
