package org.worshipsongs.domain

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Author : Madasamy
 * Version : 3.x
 */

class Topics
{
    var id: Int = 0
    var name: String? = null
    var tamilName: String? = null
    var defaultName: String? = null
    var noOfSongs: Int = 0

    constructor()
    {
        //Do nothing
    }

    constructor(name: String)
    {
        this.name = name
    }

    override fun toString(): String
    {
        return "Topics{" + "id=" + id + ", name='" + name + '\''.toString() + ", tamilName='" + tamilName + '\''.toString() + ", defaultName='" + defaultName + '\''.toString() + '}'.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is Topics)
        {
            val otherObject = `object` as Topics?
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
