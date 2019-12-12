package org.worshipsongs.domain

import org.apache.commons.lang3.builder.ToStringBuilder


/**
 * Created by Seenivasan on 3/24/2015.
 */
class AuthorSong
{

    var authorId: Int = 0
    var songId: Int = 0
    var song: Song? = null
    var author: Author? = null

    override fun toString(): String
    {
        val stringBuilder = ToStringBuilder(this)
        stringBuilder.append("authorname", author!!.name)
        stringBuilder.append("song title", song!!.title)
        return stringBuilder.toString()
    }
}
