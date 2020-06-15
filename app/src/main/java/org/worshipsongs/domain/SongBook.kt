package org.worshipsongs.domain

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Author : Madasamy
 * Version : 3.x
 */

class SongBook @JvmOverloads constructor(var name: String? = "")
{
    var id: Int = 0
    var publisher: String? = null
    var noOfSongs: Int = 0

    override fun toString(): String
    {
        return "SongBook{" + "id=" + id + ", name='" + name + '\''.toString() + ", publisher='" + publisher + '\''.toString() + '}'.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is SongBook)
        {
            val otherObject = `object` as SongBook?
            val equalsBuilder = EqualsBuilder()
            equalsBuilder.append(name, otherObject!!.name)
            return equalsBuilder.isEquals
        }
        return false

    }

    override fun hashCode(): Int
    {
        val hashCodeBuilder = HashCodeBuilder()
        hashCodeBuilder.append(name)
        return hashCodeBuilder.hashCode()
    }
}
