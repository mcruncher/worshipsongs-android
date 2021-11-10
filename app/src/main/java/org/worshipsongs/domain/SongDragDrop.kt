package org.worshipsongs.domain

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

class SongDragDrop : DragDrop
{
    var tamilTitle: String? = null

    constructor()
    {
    }

    constructor(id: Long, title: String, checked: Boolean) : super(id, title, checked)
    {
    }

    override fun toString(): String
    {
        return super.toString() + "SongDragDrop{" + "tamilTitle='" + tamilTitle + '\''.toString() + '}'.toString()
    }

    companion object
    {


        fun toJsons(items: List<SongDragDrop>): String
        {
            val gson = Gson()
            return gson.toJson(items)
        }

        fun toList(jsonString: String): List<SongDragDrop>
        {
            if (StringUtils.isNotBlank(jsonString))
            {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<SongDragDrop>>()
                {

                }.type
                return gson.fromJson(jsonString, type)
            }
            return ArrayList()
        }
    }
}
