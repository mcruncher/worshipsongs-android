package org.worshipsongs.registry

import android.os.Bundle

import org.worshipsongs.listener.SongContentViewListener

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

interface ITabFragment
{

    val title: String
    fun defaultSortOrder(): Int

    fun checked(): Boolean

    fun setListenerAndBundle(songContentViewListener: SongContentViewListener?, bundle: Bundle)
}
