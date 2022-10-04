package org.worshipsongs.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.helper.DatabaseHelper
import org.worshipsongs.utils.RegexUtils

import java.io.IOException

/**
 * @Author : Madasamy
 * @Version : 1.0
 */

class DatabaseService
{
    // private var database: SQLiteDatabase? = null
    //var database: SQLiteDatabase? = null
    private var databaseHelper: DatabaseHelper? = null


    val isDatabaseExist: Boolean
        get() = databaseHelper!!.checkDataBase()

    constructor()
    {
        //Do nothing
    }

    constructor(context: Context)
    {
        databaseHelper = DatabaseHelper(context)
    }

    var database: SQLiteDatabase? = null
        get()
        {
            return databaseHelper!!.openDataBase()
        }

    @Throws(IOException::class)
    fun copyDatabase(databasePath: String, dropDatabase: Boolean)
    {
        databaseHelper!!.createDataBase(databasePath, dropDatabase)
    }

    fun open()
    {
        database = databaseHelper!!.openDataBase()
    }

    fun close()
    {
        databaseHelper!!.close()
    }

    fun get(): SQLiteDatabase
    {
        if (database == null)
        {
            database = databaseHelper!!.openDataBase()
        }
        return database!!
    }

    fun parseTamilName(topicName: String?): String
    {
        if (topicName != null && StringUtils.isNotBlank(topicName))
        {
            val tamilTopicName = RegexUtils.getMatchString(topicName, TOPIC_NAME_REGEX)
            val formattedTopicName = tamilTopicName.replace("\\{".toRegex(), "").replace("\\}".toRegex(), "")
            return if (StringUtils.isNotBlank(formattedTopicName)) formattedTopicName else topicName
        }
        return ""
    }

    fun parseEnglishName(topicName: String?): String
    {
        return if (topicName != null && StringUtils.isNotBlank(topicName))
        {
            topicName.replace(TOPIC_NAME_REGEX.toRegex(), "")
        } else ""
    }

    companion object
    {
        val TOPIC_NAME_REGEX = "\\{.*\\}"
    }
}
