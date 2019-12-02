package org.worshipsongs.task

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import org.apache.commons.io.FileUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.service.DatabaseService
import org.worshipsongs.service.SongService
import org.worshipsongs.fragment.AlertDialogFragment

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Author : Madasamy
 * Version : 3.x
 */

class AsyncDownloadTask(private val context: AppCompatActivity) : AsyncTask<String, Int, Boolean>()
{
    private var progressBar: ProgressBar? = null
    private var destinationFile: File? = null
    private val databaseService: DatabaseService
    private val songService: SongService
    private var builder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    private var progressTextView: TextView? = null
    private val sharedPreferences: SharedPreferences

    init
    {
        databaseService = DatabaseService(context)
        songService = SongService(context)
        setDialogBuilder(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun setDialogBuilder(context: AppCompatActivity)
    {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.DialogTheme))
        val li = LayoutInflater.from(context)
        val view = li.inflate(R.layout.dialog_custom_title, null)
        val titleTextView = view.findViewById<View>(R.id.title) as TextView
        titleTextView.setText(R.string.download_song_database)
        val textView = view.findViewById<View>(R.id.subtitle) as TextView
        textView.visibility = View.GONE
        progressBar = view.findViewById<View>(R.id.progressBar2) as ProgressBar
        progressBar!!.visibility = View.VISIBLE

        val progressInitialTextSize = view.findViewById<View>(R.id.initial_text_view) as TextView
        progressInitialTextSize.visibility = View.VISIBLE

        progressTextView = view.findViewById<View>(R.id.progress_text_view) as TextView
        progressTextView!!.visibility = View.VISIBLE
        builder!!.setView(view)
    }

    override fun onPreExecute()
    {
        alertDialog = builder!!.create()
        alertDialog!!.show()
    }

    override fun doInBackground(vararg strings: String): Boolean?
    {
        try
        {
            var count: Int
            destinationFile = File(context.cacheDir.absolutePath, CommonConstants.DATABASE_NAME)
            val remoteUrl = strings[0]
            val url = URL(remoteUrl)
            val connection = url.openConnection()
            connection.readTimeout = 60000
            connection.connectTimeout = 60000
            connection.connect()
            val lengthOfFile = connection.contentLength
            val input = BufferedInputStream(url.openStream(), 10 * 1024)
            // Output stream to write file in SD card
            val output = FileOutputStream(destinationFile!!)
            val data = ByteArray(1024)
            var total: Long = 0
            publishProgress(0)
            while (input.read(data).let {
                        count = it; it != -1
                    }) {
                total += count.toLong()
                output.write(data, 0, count)
                publishProgress((total * 100 / lengthOfFile).toInt())
            }
            output.flush()
            output.close()
            input.close()
            return true
        } catch (ex: Exception)
        {
            Log.e(this.javaClass.simpleName, "Error", ex)
            return false
        }

    }

    override fun onProgressUpdate(vararg values: Int?)
    {
        super.onProgressUpdate(*values)
        progressBar!!.progress = values[0]!!
        progressTextView!!.text = values[0].toString() + "/100"
    }

    override fun onPostExecute(successfull: Boolean?)
    {
        try
        {
            alertDialog!!.cancel()
            if (successfull!!)
            {
                Log.i(AsyncDownloadTask::class.java.simpleName, "Remote database copied successfully.")
                validateDatabase(destinationFile!!.absolutePath)
            } else
            {
                showWarningDialog()
            }
        } catch (ex: Exception)
        {
            Log.e(AsyncDownloadTask::class.java.simpleName, "Error")
        } finally
        {
            context.finish()
            FileUtils.deleteQuietly(destinationFile)
        }
    }

    private fun validateDatabase(absolutePath: String)
    {
        try
        {
            databaseService.close()
            databaseService.copyDatabase(absolutePath, true)
            databaseService.open()
            if (songService.isValidDataBase)
            {
                Toast.makeText(context, R.string.message_update_song_successfull, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean(CommonConstants.UPDATED_SONGS_KEY, true).apply()
                sharedPreferences.edit().putLong(CommonConstants.NO_OF_SONGS, songService.count()).apply()
            } else
            {
                showWarningDialog()
            }
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    private fun showWarningDialog()
    {
        val bundle = Bundle()
        bundle.putString(CommonConstants.TITLE_KEY, context.getString(R.string.warning))
        bundle.putString(CommonConstants.MESSAGE_KEY, context.getString(R.string.message_configure_invalid_url))
        val alertDialogFragment = AlertDialogFragment.newInstance(bundle)
        alertDialogFragment.isCancelable = false
        alertDialogFragment.setVisibleNegativeButton(false)
        alertDialogFragment.show(context.supportFragmentManager, "WarningUpdateFragment")
    }

}
