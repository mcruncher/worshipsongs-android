package org.worshipsongs.service

import androidx.appcompat.app.AppCompatActivity


/**
 * Author : Madasamy
 * Version : 3.x
 */

interface ImportDatabaseService
{
    val name: String
    val order: Int
    fun loadDb(appCompatActivity: AppCompatActivity, objects: Map<String, Any>)

}
