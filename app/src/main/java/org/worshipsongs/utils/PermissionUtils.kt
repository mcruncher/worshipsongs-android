package org.worshipsongs.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import org.worshipsongs.CommonConstants
import org.worshipsongs.R

import android.content.ContentValues.TAG
import androidx.appcompat.app.AlertDialog

/**
 * Created by vignesh on 24/07/2017.
 */

object PermissionUtils
{
    fun isStoragePermissionGranted(activity: Activity): Boolean
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG, "Permission is granted")
                return true
            } else
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Log.i(TAG, "Never ask again")
                    val builder = AlertDialog.Builder(activity)
                    val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val titleView = inflater.inflate(R.layout.dialog_custom_title, null)
                    val titleTextView = titleView.findViewById<View>(R.id.title) as TextView
                    titleTextView.setText(R.string.permission)

                    val messageTextView = titleView.findViewById<View>(R.id.subtitle) as TextView
                    messageTextView.setTextColor(activity.resources.getColor(R.color.black_semi_transparent))
                    messageTextView.setText(R.string.storage_permission_meaage)
                    builder.setCustomTitle(titleView)
                    builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
                    builder.setPositiveButton(R.string.settings) { dialog, which ->
                        dialog.cancel()
                        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName))
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity.startActivity(myAppSettings)
                    }
                    builder.show()
                } else
                {
                    Log.v(TAG, "Permission is revoked")
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CommonConstants.STORAGE_PERMISSION_REQUEST_CODE)
                }
                return false
            }
        } else
        { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            return true
        }
    }


}
