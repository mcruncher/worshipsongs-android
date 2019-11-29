package org.worshipsongs.parser

import org.json.JSONObject

/**
 * Author : Madasamy
 * Version : 3.x
 */

class CommitMessageParser : ICommitMessageParser
{

    override fun getShaKey(result: String?): String
    {
        try
        {
            val jsonObj = JSONObject(result)
            val `object` = jsonObj.getJSONObject(OBJECT_KEY)
            return `object`.getString(SHA_KEY)
        } catch (ex: Exception)
        {
            return ""
        }

    }

    companion object
    {
        private val OBJECT_KEY = "object"
        private val SHA_KEY = "sha"
    }
}
