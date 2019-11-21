package org.worshipsongs.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.WindowManager

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.domain.Song
import org.worshipsongs.domain.SongDragDrop
import org.worshipsongs.service.DatabaseService
import org.worshipsongs.service.FavouriteService
import org.worshipsongs.service.SongService
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.PropertyUtils

import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
class SplashScreenActivity : AbstractAppCompactActivity()
{
    private var databaseService: DatabaseService? = null
    private var sharedPreferences: SharedPreferences? = null
    private var favouriteService: FavouriteService? = null
    private var songService: SongService? = null
    private var favouriteName: String? = null
    private var noOfImportedSongs = -1

    private val languageList: Array<String>
        get() = if (CommonUtils.isAboveKitkat()) arrayOf(getString(R.string.tamil_key), getString(R.string.english_key))
        else arrayOf(getString(R.string.english_key))

    private val onItemClickListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, which ->
            sharedPreferences!!.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, which).apply()
            setLocale()
        }

    private val okButtonClickListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, which ->
            if (languageList.size == 1)
            {
                sharedPreferences!!.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply()
            }
            sharedPreferences!!.edit().putBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, true).apply()
            dialog.cancel()
            moveToMainActivity()
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        initSetUp(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        migrateFavourites()
        importFavourites()
        loadDatabase()
    }

    private fun importFavourites()
    {
        val data = intent.data
        if (data != null)
        {
            val encodedString = data.query
            val decodedString = String(Base64.decode(encodedString, 0))
            val favouriteIdArray = decodedString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (favouriteIdArray != null && favouriteIdArray.size > 0)
            {
                favouriteName = favouriteIdArray[0]
                val songDragDrops = ArrayList<SongDragDrop>()
                for (i in 1 until favouriteIdArray.size)
                {
                    try
                    {
                        val song = songService!!.findById(Integer.valueOf(favouriteIdArray[i]))
                        val songDragDrop = SongDragDrop(song!!.id.toLong(), song.title, false)
                        songDragDrop.tamilTitle = song.tamilTitle
                        songDragDrops.add(songDragDrop)
                    } catch (ex: Exception)
                    {
                        Log.e(SplashScreenActivity::class.java.simpleName, "Error occurred while finding song " + ex.message)
                    }

                }
                if (songDragDrops.isEmpty())
                {
                    noOfImportedSongs = 0
                } else
                {
                    noOfImportedSongs = songDragDrops.size
                    favouriteService!!.save(favouriteName, songDragDrops)
                }
                Log.i(SplashScreenActivity::class.java.simpleName, favouriteName + " successfully imported with " + songDragDrops.size + " songs")
            }
        }
    }

    fun initSetUp(context: Context)
    {
        databaseService = DatabaseService(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        favouriteService = FavouriteService()
        songService = SongService(context)
    }

    private fun migrateFavourites()
    {
        if (sharedPreferences!!.getBoolean(CommonConstants.MIGRATION_KEY, true))
        {
            favouriteService!!.migration(this)
            sharedPreferences!!.edit().putBoolean(CommonConstants.MIGRATION_KEY, false).apply()
        }
    }

    private fun loadDatabase()
    {
        try
        {
            val context = this@SplashScreenActivity
            val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            Log.i(SplashScreenActivity::class.java.simpleName, "Current  version $currentVersion")
            copyBundleDatabase(context, currentVersion)
        } catch (e: Exception)
        {
            Log.e(this.javaClass.simpleName, "Error occurred while loading database")
        }

    }

    override fun onBackPressed()
    {
        this.finish()
        super.onBackPressed()
    }

    private fun copyBundleDatabase(context: SplashScreenActivity, currentVersion: String)
    {
        try
        {
            val commonPropertyFile = PropertyUtils.getPropertyFile(context, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME)
            val versionInPropertyFile = PropertyUtils.getProperty(CommonConstants.VERSION_KEY, commonPropertyFile)
            Log.i(SplashScreenActivity::class.java.simpleName, "Version in property file $versionInPropertyFile")
            if (CommonUtils.isNotImportedDatabase() && CommonUtils.isNewVersion(versionInPropertyFile, currentVersion))
            {
                Log.i(SplashScreenActivity::class.java.simpleName, "Preparing to copy bundle database.")
                databaseService!!.copyDatabase("", true)
                databaseService!!.open()
                PropertyUtils.setProperty(CommonConstants.VERSION_KEY, currentVersion, commonPropertyFile)
                Log.i(SplashScreenActivity::class.java.simpleName, "Bundle database copied successfully.")
            }
            showLanguageSelectionDialog()
        } catch (ex: Exception)
        {
            Log.e(this.javaClass.simpleName, "Error occurred while coping databases", ex)
        }

    }

    private fun showLanguageSelectionDialog()
    {
        val languageChoosed = sharedPreferences!!.getBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, false)
        val index = sharedPreferences!!.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0)
        if (!languageChoosed)
        {
            val builder = AlertDialog.Builder(this)
            val languageList = languageList
            builder.setSingleChoiceItems(languageList, index, onItemClickListener)
            builder.setPositiveButton(R.string.ok, okButtonClickListener)
            builder.setTitle(R.string.language_title)
            builder.setCancelable(false)
            builder.show()
        } else
        {
            moveToMainActivity()
        }
    }

    private fun moveToMainActivity()
    {
        setLocale()
        val intent = Intent(this@SplashScreenActivity, NavigationDrawerActivity::class.java)
        intent.putExtra(CommonConstants.FAVOURITES_KEY, noOfImportedSongs)
        startActivity(intent)
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out)
        this@SplashScreenActivity.finish()
    }

    private fun setLocale()
    {
        val index = sharedPreferences!!.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0)
        val localeCode = if (index == 0) "ta" else "en"
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}