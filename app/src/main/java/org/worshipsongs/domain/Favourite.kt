package org.worshipsongs.domain

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import java.util.ArrayList

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

class Favourite : Comparable<Favourite>
{
    var orderId: Int = 0
    var name: String? = null
    var dragDrops: MutableList<SongDragDrop>? = null

    constructor()
    {

    }

    constructor(name: String, dragDrop: MutableList<SongDragDrop>) : this(0, name, dragDrop)
    {
    }

    constructor(orderId: Int, name: String, dragDrops: MutableList<SongDragDrop>)
    {
        this.orderId = orderId
        this.name = name
        this.dragDrops = dragDrops
    }

    override fun toString(): String
    {
        return "Favourite{" + "name='" + name + '\''.toString() + ", dragDrops=" + dragDrops + '}'.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is Favourite)
        {
            val otherObject = `object` as Favourite?
            val equalsBuilder = EqualsBuilder()
            equalsBuilder.append(otherObject!!.name, name)
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

    override fun compareTo(other: Favourite): Int
    {
        return if (other.orderId < orderId) -1 else if (orderId == other.orderId) 0 else 1
    }

    companion object
    {

        fun toJson(items: List<Favourite>): String
        {
            val gson = Gson()
            return gson.toJson(items)
        }

        fun toArrays(jsonString: String): List<Favourite>
        {
            if (StringUtils.isNotBlank(jsonString))
            {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<Favourite>>()
                {

                }.type
                return gson.fromJson(jsonString, type)
            }
            return ArrayList()
        }
    }
}
