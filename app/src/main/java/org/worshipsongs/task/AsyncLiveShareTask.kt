package org.worshipsongs.task;

import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.io.FileUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.utils.UnzipUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

public class AsyncLiveShareTask(private val context: AppCompatActivity) : AsyncTask<String, Int, Boolean>()
{
    private var progressBar: ProgressBar? = null
    private var destinationFile: File? = null
    private var builder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    private var progressTextView: TextView? = null
    private val sharedPreferences: SharedPreferences

    init
    {
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
            destinationFile = File(context.cacheDir.absolutePath, "liveshare")
            val remoteUrl = sharedPreferences.getString(CommonConstants.LIVE_SHARE_PATH_KEY, "")
            val url = URL(remoteUrl)
            val connection = url.openConnection()
            connection.readTimeout = 60000
            connection.connectTimeout = 60000
            connection.connect()
            val lengthOfFile = connection.contentLength
            val input = BufferedInputStream(url.openStream(), 10 * 1024)
            // Output stream to write file in SD card
            val output = FileOutputStream(destinationFile!!)
            val data = ByteArray(10)
            var total: Long = 0
            publishProgress(0)
            Log.e(this.javaClass.simpleName, "Length of file $lengthOfFile")
            while (input.read(data).let {
                        count = it; it != -1
                    }) {
                total += count.toLong()
                output.write(data, 0, count)
                Log.i(this.javaClass.simpleName, "publish")
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
                var tempLiveShareDir = File(context.cacheDir.absolutePath, "tempLiveShare")
                UnzipUtils.unzip(destinationFile!!, tempLiveShareDir, false)
                var tempServiceDir = File(context.cacheDir.absolutePath, "tempService")
                UnzipUtils.unZipServices(tempLiveShareDir, tempServiceDir)
                var serviceDir = "/data/data/" + context.applicationContext.packageName + "/databases/service"
                FileUtils.copyDirectory(tempServiceDir, File(serviceDir))
                FileUtils.deleteQuietly(tempLiveShareDir)
                FileUtils.deleteQuietly(tempServiceDir)
                Log.i(AsyncDownloadTask::class.java.simpleName, "Services ${UnzipUtils.getServiceNames(serviceDir)}")
            }
        } catch (ex: Exception)
        {
            Log.e(AsyncDownloadTask::class.java.simpleName, "Error")
        } finally
        {
            FileUtils.deleteQuietly(destinationFile)
        }
    }

}

