package org.worshipsongs.service

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.activity.SplashScreenActivity
import org.worshipsongs.dialog.CustomDialogBuilder
import org.worshipsongs.domain.DialogConfiguration
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

/**
 * Author : Madasamy
 * Version : 3.x
 */

class RemoteImportDatabaseService : ImportDatabaseService
{

    private var objects: Map<String, Any>? = null

    private var appCompatActivity: AppCompatActivity? = null
    private var sharedPreferences: SharedPreferences? = null
    private var remoteUrl: String? = null

    private var songService: SongService? = null
    private var databaseService: DatabaseService? = null

    override val name: String
        get() = RemoteImportDatabaseService::class.java.simpleName

    override val order: Int
        get() = 0

    private val isWifiOrMobileDataConnectionExists: Boolean
        get()
        {
            val connectivityManager = appCompatActivity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null)
            {
                if (networkInfo.isConnected)
                {
                    if (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
                    {
                        return true
                    }
                }
            }
            return false
        }

    val countQueryResult: String
        get()
        {
            val count = songService!!.count().toString()
            return String.format(appCompatActivity!!.getString(R.string.songs_count), count)
        }

    override fun loadDb(appCompatActivity: AppCompatActivity, objects: Map<String, Any>)
    {
        this.appCompatActivity = appCompatActivity
        songService = SongService(appCompatActivity)
        databaseService = DatabaseService(appCompatActivity)
        this.objects = objects
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity)
        setDefaultRemoteUrl()
        if (isWifiOrMobileDataConnectionExists)
        {
            showRemoteUrlConfigurationDialog()
        } else
        {
            showNetWorkWarningDialog()
        }
    }

    private fun setDefaultRemoteUrl()
    {
        if (!sharedPreferences!!.all.containsKey(CommonConstants.REMOTE_URL))
        {
            sharedPreferences!!.edit().putString(CommonConstants.REMOTE_URL, appCompatActivity!!.getString(R.string.remoteUrl)).apply()
        }
    }

    private fun showRemoteUrlConfigurationDialog()
    {
        val dialogConfiguration = DialogConfiguration(appCompatActivity!!.getString(R.string.url), "")
        dialogConfiguration.isEditTextVisibility = true
        val customDialogBuilder = CustomDialogBuilder(appCompatActivity!!, dialogConfiguration)
        val editText = customDialogBuilder.editText
        editText!!.setText(sharedPreferences!!.getString(CommonConstants.REMOTE_URL, appCompatActivity!!.getString(R.string.remoteUrl)))
        val builder = customDialogBuilder.builder
        builder!!.setPositiveButton(R.string.ok) { dialog, which ->
            val url = editText.text.toString()
            AsyncDownloadTask().execute(url)
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun showNetWorkWarningDialog()
    {
        val dialogConfiguration = DialogConfiguration(appCompatActivity!!.getString(R.string.warning), appCompatActivity!!.getString(R.string.message_network_warning))
        val customDialogBuilder = CustomDialogBuilder(appCompatActivity!!, dialogConfiguration)
        customDialogBuilder.builder!!.setPositiveButton(R.string.ok) { dialog, which -> dialog.cancel() }
        customDialogBuilder.builder!!.show()
    }

    private inner class AsyncDownloadTask : AsyncTask<String, Void, Boolean>()
    {
        private var destinationFile: File? = null
        private val progressDialog = ProgressDialog(ContextThemeWrapper(appCompatActivity, R.style.DialogTheme))
        private val resultTextView = objects!![CommonConstants.TEXTVIEW_KEY] as TextView?

        override fun onPreExecute()
        {
            progressDialog.setTitle(appCompatActivity!!.getString(R.string.downloading_title))
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(false)
            progressDialog.max = 100
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: String): Boolean?
        {
            try
            {
                var count: Int
                destinationFile = File(appCompatActivity!!.cacheDir.absolutePath, CommonConstants.DATABASE_NAME)
                remoteUrl = strings[0]
                val url = URL(remoteUrl)
                val conection = url.openConnection()
                conection.readTimeout = 60000
                conection.connectTimeout = 60000
                conection.connect()
                val lenghtOfFile = conection.contentLength
                val input = BufferedInputStream(url.openStream(), 10 * 1024)
                // Output stream to write file in SD card
                val output = FileOutputStream(destinationFile!!)
                val data = ByteArray(1024)
                var total: Long = 0
                progressDialog.progress = 0
                while(input.read(data).let { count=it; it !=-1 }) {
                    total += count.toLong()
                    output.write(data, 0, count)
                    progressDialog.progress = (total * 100 / lenghtOfFile).toInt()
                }
                output.flush()
                output.close()
                input.close()
                return true
            } catch (ex: Exception)
            {
                Log.e(this.javaClass.simpleName, "Error", ex)

                return false
            } finally
            {
                destinationFile!!.deleteOnExit()
            }
        }

        override fun onPostExecute(successfull: Boolean)
        {
            resultTextView!!.text = ""
            if (successfull)
            {
                Log.i(SplashScreenActivity::class.java.simpleName, "Remote database copied successfully.")
                validateDatabase(destinationFile!!.absolutePath, resultTextView)
            } else
            {
                showWarningDialog(resultTextView)
            }
            progressDialog.cancel()
        }
    }

    private fun validateDatabase(absolutePath: String, resultTextView: TextView)
    {
        try
        {
            databaseService!!.close()
            databaseService!!.copyDatabase(absolutePath, true)
            databaseService!!.open()
            if (songService!!.isValidDataBase)
            {
                val revertDatabaseButton = objects!![CommonConstants.REVERT_DATABASE_BUTTON_KEY] as Button?
                sharedPreferences!!.edit().putBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, true).apply()
                revertDatabaseButton!!.visibility = if (sharedPreferences!!.getBoolean(CommonConstants.SHOW_REVERT_DATABASE_BUTTON_KEY, false)) View.VISIBLE else View.GONE
                resultTextView.text = countQueryResult
                sharedPreferences!!.edit().putString(CommonConstants.REMOTE_URL, remoteUrl).apply()
                sharedPreferences!!.edit().remove(CommonConstants.COMMIT_SHA_KEY).apply()
                Toast.makeText(appCompatActivity, appCompatActivity!!.getString(R.string.import_database_successfull), Toast.LENGTH_SHORT).show()
            } else
            {
                showWarningDialog(resultTextView)
            }
        } catch (e: IOException)
        {
            e.printStackTrace()
        }

    }

    private fun showWarningDialog(resultTextView: TextView)
    {
        val dialogConfiguration = DialogConfiguration(appCompatActivity!!.getString(R.string.warning), appCompatActivity!!.getString(R.string.message_invalid_url))
        val customDialogBuilder = CustomDialogBuilder(appCompatActivity!!, dialogConfiguration)
        customDialogBuilder.builder!!.setPositiveButton(R.string.ok) { dialog, which ->
            try
            {
                databaseService!!.close()
                databaseService!!.copyDatabase("", true)
                databaseService!!.open()
                resultTextView.text = countQueryResult
                dialog.cancel()
            } catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
        customDialogBuilder.builder!!.show()
    }

}
