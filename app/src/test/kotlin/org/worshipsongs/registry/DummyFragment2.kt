package org.worshipsongs.registry

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.worshipsongs.listener.SongContentViewListener

/**
 * Author : Madasamy
 * Version : 3.x.x
 */
class DummyFragment2 : Fragment(), ITabFragment {
    override fun defaultSortOrder(): Int {
        return 2
    }

    override val title: String
        get() = "song_books"

    override fun checked(): Boolean {
        return false
    }

    override fun setListenerAndBundle(
        songContentViewListener: SongContentViewListener?,
        bundle: Bundle
    ) {
    }
}