package org.worshipsongs.service

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity


import org.worshipsongs.service.ImportDatabaseService

/**
 * Author : Madasamy
 * Version : 3.x
 */

class ExternalImportDatabaseService : ImportDatabaseService
{

    private var objects: Map<String, Any>? = null
    private var appCompatActivity: AppCompatActivity? = null

    override fun loadDb(appCompatActivity: AppCompatActivity, objects: Map<String, Any>)
    {
        this.appCompatActivity = appCompatActivity
        this.objects = objects
        showFileChooser()
    }

    private fun showFileChooser()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri = Uri.parse(Environment.getExternalStorageDirectory().path)
        intent.setDataAndType(uri, "*/*")
        try
        {
            appCompatActivity!!.startActivityForResult(Intent.createChooser(intent, "Select a File to Import"), 1)
        } catch (ex: android.content.ActivityNotFoundException)
        {
        }
    }

    override val name: String get() = this.javaClass.simpleName

    override val order: Int get() = 1
}
