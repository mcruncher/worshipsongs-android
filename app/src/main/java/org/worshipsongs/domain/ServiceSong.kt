package org.worshipsongs.domain

/**
 * @author Vignesh Palanisamy
 * @version 3.x
 */

class ServiceSong(var title: String?, var song: Song?)
{

    override fun toString(): String
    {
        return "ServiceSong{" + "title='" + title + '\''.toString() + ", song=" + song + '}'.toString()
    }
}
