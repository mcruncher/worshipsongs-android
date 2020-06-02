package org.worshipsongs.registry

import android.os.Bundle

import org.worshipsongs.listener.SongContentViewListener

/**
 * @author: Madasamy
 * @since: 3.x
 */

interface ITabFragment
{

    val title: String
    fun defaultSortOrder(): Int

    fun checked(): Boolean

    fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
}
