package org.worshipsongs.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.worshipsongs.R
import org.worshipsongs.listener.SongContentViewListener
import org.worshipsongs.registry.ITabFragment

class LiveShareFragment : Fragment(), ITabFragment {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.songs_layout, container, false)
      //  setInfoTextView(view)
        //setListView(view)
        return view
    }

    override val title: String
        get() = "live_share"

    override fun defaultSortOrder(): Int {
        return 5
    }

    override fun checked(): Boolean {
        return true
    }

    override fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle) {

    }
}