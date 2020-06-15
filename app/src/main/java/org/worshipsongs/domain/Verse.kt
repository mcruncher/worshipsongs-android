package org.worshipsongs.domain

import org.apache.commons.lang3.builder.ToStringBuilder

import java.io.Serializable

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
class Verse : Serializable
{
    var type: String? = null
    var label: Int = 0
    var content: String? = null
    private val verseOrder: String? = null

    fun getVerseOrder(): String?
    {
        return content
    }

    fun setVerseOrder(content: String)
    {
        this.content = content
    }

    override fun toString(): String
    {
        return ToStringBuilder.reflectionToString(this)
    }
}
