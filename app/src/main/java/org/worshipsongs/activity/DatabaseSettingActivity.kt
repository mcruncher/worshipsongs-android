package org.worshipsongs.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.OpenableColumns
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.fragment.AlertDialogFragment
import org.worshipsongs.locator.ImportDatabaseLocator
import org.worshipsongs.service.DatabaseService
import org.worshipsongs.service.PresentationScreenService
import org.worshipsongs.service.SongService
import org.worshipsongs.utils.PermissionUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Author : Madasamy
 * Version : 3.x
 */

class DatabaseSettingActivity : AbstractAppCompactActivity(), AlertDialogFragment.DialogListener
{
    private val importDatabaseLocator = ImportDatabaseLocator()
    private val songService = SongService(WorshipSongApplication.context!!)
    private val databaseService = DatabaseService(WorshipSongApplication.context!!)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.context)
    private var presentationScreenService: PresentationScreenService? = null
    private var defaultDatabaseButton: Button? = null
    private var resultTextView: TextView? = null

    val destinationFile: File
        get() = File(cacheDir.absolutePath, CommonConstants.DATABASE_NAME)

    val countQueryResult: String
        get()
        {
            var count: String? = null
            try
            {
                count = songService.count().toString()
            } catch (e: Exception)
            {
                count = ""
            }

            return String.format(getString(R.string.songs_count), count)
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.database_layout)
        setActionBar()
        setImportDatabaseButton()
        setDefaultDatabaseButton()
        setResultTextView()
        presentationScreenService = PresentationScreenService(this)
    }


    private fun setActionBar()
    {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle(R.string.database)
    }

    private fun setImportDatabaseButton()
    {
        val importDatabaseButton = findViewById<View>(R.id.upload_database_button) as Button
        importDatabaseButton.setOnClickListener(ImportDatabaseOnClickListener())
    }


    private inner class ImportDatabaseOnClickListener : View.OnClickListener
    {
        override fun onClick(view: View)
        {
            if (PermissionUtils.isStoragePermissionGranted(this@DatabaseSettingActivity))
            {
                showDatabaseTypeDialog()
            }
        }
    }

    private fun showDatabaseTypeDialog()
    {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.DialogTheme))
        builder.setTitle(getString(R.string.type))
        builder.setItems(R.array.dataBaseTypes) { dialog, which ->
            importDatabaseLocator.load(this@DatabaseSettingActivity, getStringObjectMap(which))
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.listView.setSelector(android.R.color.darker_gray)
        dialog.show()
    }

    private fun getStringObjectMap(which: Int): Map<String, Any>
    {
        val objectMap = HashMap<String, Any>()
        objectMap[CommonConstants.INDEX_KEY] = which
        objectMap[CommonConstants.TEXTVIEW_KEY] = resultTextView!!
        objectMap[CommonConstants.REVERT_DATABASE_BUTTON_KEY] = defaultDatabaseButton!!
        return objectMap
    }

    private fun setDefaultDatabaseButton()
    {
        defaultDatabaseButton = findViewById<View>(R.id.default_database_button) as Button
        defaultDatabaseButton!!.visibility = if (sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false)) View.VISIBLE
        else View.GONE
        defaultDatabaseButton!!.setOnClickListener(DefaultDbOnClickListener())
    }

    private inner class DefaultDbOnClickListener : View.OnClickListener
    {
        override fun onClick(v: View)
        {
            val bundle = Bundle()
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.reset_default_title))
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_database_confirmation))
            val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
            alertDialogFragment.setDialogListener(this@DatabaseSettingActivity)
            alertDialogFragment.show(supportFragmentManager, "RevertDefaultDatabaseDialog")
        }
    }

    private fun setResultTextView()
    {
        resultTextView = findViewById<View>(R.id.result_textview) as TextView
        resultTextView!!.text = countQueryResult
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            android.R.id.home -> finish()
            else ->
            {
            }
        }
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?)
    {
        when (requestCode)
        {
            1 -> if (resultCode == Activity.RESULT_OK)
            {
                val uri = intent!!.data
                doCopyFile(uri, getFileName(uri!!))
            }
            else ->
            {
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    private fun doCopyFile(uri: Uri?, fileName: String)
    {
        try
        {
            val fileExtension = FilenameUtils.getExtension(fileName)
            if ("sqlite".equals(fileExtension, ignoreCase = true))
            {
                showConfirmationDialog(uri!!, fileName)
            } else
            {
                copyFile(uri)
            }
        } finally
        {
            destinationFile.deleteOnExit()
        }
    }

    private fun showConfirmationDialog(uri: Uri, fileName: String)
    {
        val bundle = Bundle()
        bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.confirmation))
        bundle.putString(CommonConstants.MESSAGE_KEY, String.format(getString(R.string.message_choose_local_db_confirmation), fileName))
        bundle.putString(CommonConstants.NAME_KEY, uri.toString())
        val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
        alertDialogFragment.setVisiblePositiveButton(true)
        alertDialogFragment.setVisibleNegativeButton(true)
        alertDialogFragment.setDialogListener(this)
        alertDialogFragment.show(supportFragmentManager, "DatabaseImportConfirmation")
    }


    private fun copyFile(uri: Uri?)
    {
        try
        {
            val destinationFile = destinationFile
            val inputStream = contentResolver.openInputStream(uri!!)
            val outputstream = FileOutputStream(destinationFile)
            val data = ByteArray(inputStream!!.available())
            inputStream.read(data)
            outputstream.write(data)
            inputStream.close()
            outputstream.close()
            Log.i(this@DatabaseSettingActivity.javaClass.simpleName, "Size of file " + FileUtils.sizeOf(destinationFile))
            validateDatabase(destinationFile.absolutePath)
        } catch (ex: IOException)
        {
            Log.i(DatabaseSettingActivity::class.java.simpleName, "Error occurred while coping file$ex")
        }

    }

    private fun getFileName(uri: Uri): String
    {
        val selectedFile = File(uri.toString())
        var fileName = ""
        if (uri.toString().startsWith("content://"))
        {
            var cursor: Cursor? = null
            try
            {
                cursor = contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst())
                {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally
            {
                cursor!!.close()
            }
        } else
        {
            fileName = selectedFile.name
        }
        return fileName
    }

    private fun validateDatabase(absolutePath: String)
    {
        try
        {
            resultTextView!!.text = ""
            databaseService.close()
            databaseService.copyDatabase(absolutePath, true)
            databaseService.open()
            if (songService.isValidDataBase)
            {
                updateResultTextview()
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply()
                defaultDatabaseButton!!.visibility = if (sharedPreferences.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false)) View.VISIBLE
                else View.GONE
                Toast.makeText(this, R.string.import_database_successfull, Toast.LENGTH_SHORT).show()
            } else
            {
                showWarningDialog()
            }
        } catch (e: IOException)
        {
            e.printStackTrace()
            Log.i(this@DatabaseSettingActivity.javaClass.simpleName, "Error occurred while coping external db$e")
        }

    }

    private fun updateResultTextview()
    {
        resultTextView!!.text = ""
        resultTextView!!.text = countQueryResult
    }

    private fun showWarningDialog()
    {
        val bundle = Bundle()
        bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning))
        bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_database_invalid))
        val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
        alertDialogFragment.setDialogListener(this)
        alertDialogFragment.setVisibleNegativeButton(false)
        alertDialogFragment.isCancelable = false
        alertDialogFragment.show(supportFragmentManager, "InvalidLocalDbWaringDialog")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            CommonConstants.STORAGE_PERMISSION_REQUEST_CODE ->
            {
                onRequestPermissionsResult(grantResults)
                return
            }
            else ->
            {
            }
        }
    }

    protected fun onRequestPermissionsResult(grantResults: IntArray)
    {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            showDatabaseTypeDialog()
        } else
        {
            Log.i(this.javaClass.simpleName, "Permission denied")
        }
    }

    override fun onClickPositiveButton(bundle: Bundle?, tag: String?)
    {
        if ("DatabaseImportConfirmation".equals(tag, ignoreCase = true))
        {
            val uriString = bundle!!.getString(CommonConstants.NAME_KEY)
            val uri = Uri.parse(uriString)
            copyFile(uri)
        } else if ("InvalidLocalDbWaringDialog".equals(tag, ignoreCase = true))
        {
            try
            {
                databaseService.close()
                databaseService.copyDatabase("", true)
                databaseService.open()
                updateResultTextview()
            } catch (e: IOException)
            {
                Log.e(DatabaseSettingActivity::class.java.simpleName, "Error", e)
            }

        } else if ("RevertDefaultDatabaseDialog".equals(tag, ignoreCase = true))
        {
            try
            {
                databaseService.close()
                databaseService.copyDatabase("", true)
                databaseService.open()
                updateResultTextview()
                sharedPreferences.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false).apply()
                defaultDatabaseButton!!.visibility = View.GONE
            } catch (ex: IOException)
            {
                Log.e(this@DatabaseSettingActivity.javaClass.simpleName, "Error occurred while coping database $ex")
            }

        }
    }

    override fun onClickNegativeButton()
    {
        //Do nothing
    }

    override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }

    override fun onPause()
    {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onStop()
    {
        super.onStop()
        presentationScreenService!!.onStop()
    }

}
