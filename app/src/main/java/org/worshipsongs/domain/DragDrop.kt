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

open class DragDrop
{
    var sortOrder: Long = 0
    var title: String? = null
    var isChecked: Boolean = false

    constructor()
    {
    }

    constructor(sortOrder: Long, title: String, checked: Boolean)
    {
        this.sortOrder = sortOrder
        this.title = title
        this.isChecked = checked
    }

    override fun toString(): String
    {
        return "DragDrop{" + "sortOrder=" + sortOrder + ", title='" + title + '\''.toString() + ", checked=" + isChecked + '}'.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is DragDrop)
        {
            val otherObject = `object` as DragDrop?
            val builder = EqualsBuilder()
            builder.append(title, otherObject!!.title)
            return builder.isEquals
        }
        return false
    }

    override fun hashCode(): Int
    {
        val hashCodeBuilder = HashCodeBuilder()
        hashCodeBuilder.append(title)
        return hashCodeBuilder.hashCode()
    }

    companion object
    {

        fun toJson(items: List<DragDrop>): String
        {
            val gson = Gson()
            return gson.toJson(items)
        }

        fun toArrays(jsonString: String): ArrayList<DragDrop>
        {
            if (StringUtils.isNotBlank(jsonString))
            {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<DragDrop>>()
                {

                }.type
                return gson.fromJson(jsonString, type)
            }
            return ArrayList()
        }
    }

}
