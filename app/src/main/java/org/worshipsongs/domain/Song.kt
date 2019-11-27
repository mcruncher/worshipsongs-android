package org.worshipsongs.domain

import android.os.Parcel
import android.os.Parcelable

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

import java.util.ArrayList
import java.util.Comparator

/**
 * @Author : Madasamy
 * @Version : 1.0
 */

class Song
{
    var id: Int = 0
    var songBookId: Int = 0
    var title: String? = null
    var alternateTitle: String? = null
    var tamilTitle: String? = null
    var lyrics: String? = null
    var verseOrder: String? = null
    var copyright: String? = null
    var comments: String? = null
    var ccliNumber: String? = null
    var songNumber: String? = null
    var themeName: String? = null
    var searchTitle: String? = null
    var searchLyrics: String? = null
    var createdDate: String? = null
    var lastModified: String? = null
    private var temporary: Boolean = false
    var urlKey: String? = null
    var contents: List<String>? = null
    var chord: String? = null
    var authorName: String? = null
    var songBookNumber: Int = 0

    constructor()
    {
        //Do nothing
    }

    constructor(title: String)
    {
        this.title = title
    }

    fun isTemporary(): Boolean
    {
        return temporary
    }

    fun getTemporary(): Boolean
    {
        return temporary
    }

    fun setTemporary(temporary: Boolean)
    {
        this.temporary = temporary
    }

    override fun toString(): String
    {
        val stringBuilder = ToStringBuilder(this)
        stringBuilder.append("title", title)
        stringBuilder.append("verse order", verseOrder)
        return stringBuilder.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is Song)
        {
            val otherObject = `object` as Song?
            val equalsBuilder = EqualsBuilder()
            equalsBuilder.append(title, otherObject!!.title)
            equalsBuilder.append(searchTitle, otherObject.searchTitle)
            return equalsBuilder.isEquals
        }

        return false
    }

    override fun hashCode(): Int
    {
        val hashCodeBuilder = HashCodeBuilder()
        hashCodeBuilder.append(title)
        hashCodeBuilder.append(searchTitle)
        return hashCodeBuilder.hashCode()
    }

    companion object
    {

        val SONG_BOOK_NUMBER_ASC: Comparator<Song> = Comparator { song1, song2 ->
            val firstSongBookNumber = if (song1.songBookNumber == 0) 10000 else song1.songBookNumber
            val secondSongBookNumber = if (song2.songBookNumber == 0) 10001 else song2.songBookNumber
            firstSongBookNumber - secondSongBookNumber
        }
    }
}
