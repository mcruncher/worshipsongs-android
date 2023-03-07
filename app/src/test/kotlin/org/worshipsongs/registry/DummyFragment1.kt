package org.worshipsongs.registry

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.worshipsongs.listener.SongContentViewListener

/**
 * @author  Madasamy
 * @since 3.x.x
 */
class DummyFragment1 : Fragment(), ITabFragment {
    override fun defaultSortOrder(): Int {
        return 1
    }

    override val title: String
        get() = "title"

    override fun checked(): Boolean {
        return true
    }

    override fun setListenerAndBundle(
        songContentViewListener: SongContentViewListener?,
        bundle: Bundle
    ) {
    }
}