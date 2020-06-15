package org.worshipsongs.domain

import android.view.View

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Author : Madasamy
 * Version : 3.x
 */

class DialogConfiguration
{
    var title: String? = null
    var message = ""

    var isEditTextVisibility: Boolean = false

    constructor()
    {
        //Do nothing
    }

    constructor(title: String, message: String)
    {
        this.title = title
        this.message = message
    }

    override fun toString(): String
    {
        return "DialogConfiguration{" + "title='" + title + '\''.toString() + ", message='" + message + '\''.toString() + '}'.toString()
    }

    override fun equals(`object`: Any?): Boolean
    {
        if (`object` is DialogConfiguration)
        {
            val otherObject = `object` as DialogConfiguration?
            val builder = EqualsBuilder()
            builder.append(otherObject!!.title, title)
            return builder.isEquals
        }
        return false
    }

    override fun hashCode(): Int
    {
        val codeBuilder = HashCodeBuilder()
        codeBuilder.append(title)
        return codeBuilder.hashCode()
    }
}
