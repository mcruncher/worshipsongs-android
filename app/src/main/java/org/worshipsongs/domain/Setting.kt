package org.worshipsongs.domain

import android.view.Display

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

/**
 * author: madasamy
 * version: 2.1.0
 */
class Setting private constructor()
{
    var position: Int = 0
    var slidePosition: Int = 0
    var song: Song? = null

    override fun toString(): String
    {
        val stringBuilder = ToStringBuilder(this)
        stringBuilder.append("position", position)
        return stringBuilder.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is Setting)
        {
            val otherObject = `object` as Setting?
            val equalsBuilder = EqualsBuilder()
            equalsBuilder.append(position, otherObject!!.position)
            return equalsBuilder.isEquals
        }
        return false
    }

    override fun hashCode(): Int
    {
        val hashCodeBuilder = HashCodeBuilder()
        hashCodeBuilder.append(position)
        return hashCodeBuilder.hashCode()
    }

    companion object
    {
        val instance = Setting()
    }
}
