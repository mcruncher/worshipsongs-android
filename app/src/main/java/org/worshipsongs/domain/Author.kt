package org.worshipsongs.domain

import org.apache.commons.lang3.builder.ToStringBuilder

import java.io.Serializable

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
class Author
{
    var id: Int = 0
    var firstName: String? = null
    var lastName: String? = null
    var name: String? = null
    var tamilName: String? = null
    var defaultName: String? = null
    var noOfSongs: Int = 0

    constructor()
    {

    }

    constructor(name: String)
    {
        this.name = name
    }

    override fun toString(): String
    {
        val builder = ToStringBuilder(this)
        builder.append(super.toString())
        builder.append("firstname", firstName)
        builder.append("lastName", lastName)
        return builder.toString()
    }
}
