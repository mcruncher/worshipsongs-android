package org.worshipsongs.service

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.Favourite
import org.worshipsongs.domain.Song
import org.worshipsongs.domain.SongDragDrop
import org.worshipsongs.utils.PropertyUtils

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.HashSet

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

class FavouriteService
{

    private var sharedPreferences: SharedPreferences? = null
    private var songService: SongService? = null

    constructor()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.context)
        songService = SongService(WorshipSongApplication.context!!)
    }

    constructor(context: Context?)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context
                ?: WorshipSongApplication.context)
    }

    fun migration(context: Context)
    {
        Log.i(FavouriteService::class.java.simpleName, "Preparing to migrate...")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val serviceFileName = PropertyUtils.getPropertyFile(context, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME)
        val services = PropertyUtils.getServices(serviceFileName!!)
        val favouriteList = ArrayList<Favourite>()
        for (i in services.indices)
        {
            val songTitleString = PropertyUtils.getProperty(services[i], serviceFileName)
            val songTitles = songTitleString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dragDrops = ArrayList<SongDragDrop>()
            for (j in songTitles.indices)
            {
                val songDragDrop = SongDragDrop(j.toLong(), songTitles[j], false)
                songDragDrop.tamilTitle = ""
                dragDrops.add(songDragDrop)
                Log.i(FavouriteService::class.java.simpleName, "No. of songs " + dragDrops.size)
            }
            favouriteList.add(Favourite(i, services[i], dragDrops))
        }
        Log.i(FavouriteService::class.java.simpleName, "No. of services " + favouriteList.size)
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(favouriteList)).apply()
    }

    fun save(serviceName: String, songDragDrop: SongDragDrop)
    {
        val favouriteString = sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")
        val favourites = Favourite.toArrays(favouriteString!!)
        val existingFavourite = find(favourites, serviceName)
        val favouriteSet = HashSet<Favourite>()
        favouriteSet.addAll(favourites)
        if (StringUtils.isNotBlank(existingFavourite.name))
        {
            val dragDrops = existingFavourite.dragDrops
            dragDrops!!.add(songDragDrop)
            existingFavourite.dragDrops = dragDrops
            favouriteSet.add(existingFavourite)
        } else
        {
            val dragDrops = ArrayList<SongDragDrop>()
            dragDrops.add(songDragDrop)
            favouriteSet.add(Favourite(getFavouritesNewOrderNumber(favourites), serviceName, dragDrops))
        }
        val uniqueFavourites = ArrayList(favouriteSet)
        sharedPreferences!!.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(uniqueFavourites)).apply()
    }

    fun getFavouritesNewOrderNumber(favourites: List<Favourite>): Int
    {
        if (favourites.isEmpty())
        {
            return 0
        } else
        {
            Collections.sort(favourites)
            return favourites[0].orderId + 1
        }
    }

    fun save(name: String, dragDrops: MutableList<SongDragDrop>)
    {
        val favouriteString = sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")
        val favourites = Favourite.toArrays(favouriteString!!)
        var existingFavourite = find(favourites, name)
        val favouriteSet = HashSet<Favourite>()
        favouriteSet.addAll(favourites)
        if (StringUtils.isNotBlank(existingFavourite.name))
        {
            existingFavourite.dragDrops = dragDrops
        } else
        {
            existingFavourite = Favourite(getFavouritesNewOrderNumber(favourites), name, dragDrops)
        }
        favouriteSet.add(existingFavourite)
        val uniqueFavourites = ArrayList(favouriteSet)
        sharedPreferences!!.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(uniqueFavourites)).apply()
    }

    fun find(name: String): Favourite
    {
        val favourites = Favourite.toArrays(sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")!!)
        return find(favourites, name)
    }

    private fun find(favourites: List<Favourite>, favouriteName: String): Favourite
    {
        for (favourite in favourites)
        {
            if (favourite.name!!.equals(favouriteName, ignoreCase = true))
            {
                return favourite
            }
        }
        return Favourite()
    }

    fun findNames(): MutableList<String>
    {
        val favourites = Favourite.toArrays(sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")!!)
        Collections.sort(favourites)
        val names = mutableListOf<String>()
        for (favourite in favourites)
        {
            names.add(favourite.name!!)
        }
        return names
    }

    fun buildShareFavouriteFormat(name: String): String
    {
        val favourite = find(name)
        val linkBuilder = StringBuilder()
        linkBuilder.append(name).append(";")
        val builder = StringBuilder()
        builder.append(name).append("\n\n")
        val dragDrops = favourite.dragDrops
        for (i in dragDrops!!.indices)
        {
            val songDragDrop = dragDrops[i]
            builder.append(i + 1).append(". ").append(if (StringUtils.isNotBlank(songDragDrop.tamilTitle)) songDragDrop.tamilTitle!! + "\n"
                    else "").append(songDragDrop.title).append("\n\n")
            if (songDragDrop.id > 0)
            {
                linkBuilder.append(songDragDrop.id).append(";")
            } else
            {
                val song = songService!!.findByTitle(songDragDrop.title!!)
                linkBuilder.append(song!!.id).append(";")
            }
        }
        val base64String = Base64.encodeToString(linkBuilder.toString().toByteArray(), 0)
        builder.append("\n").append("https://mcruncher.github.io/worshipsongs/?").append(base64String)
        return builder.toString()
    }

    fun findSongsByFavouriteName(name: String): List<Song>
    {
        val songs = ArrayList<Song>()
        val favourite = find(name)
        for (songDragDrop in favourite.dragDrops!!)
        {
            val song = songService!!.findContentsByTitle(songDragDrop.title!!)
            if (song != null)
            {
                songs.add(song)
            }
        }
        return songs
    }

    fun remove(name: String)
    {
        val favouriteString = sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")
        val favourites = Favourite.toArrays(favouriteString!!)
        val favourite = find(favourites, name)
        favourites.remove(favourite)
        sharedPreferences!!.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(favourites)).apply()
    }

    fun removeSong(name: String, songName: String)
    {
        val favouriteString = sharedPreferences!!.getString(CommonConstants.FAVOURITES_KEY, "")
        val favourites = Favourite.toArrays(favouriteString!!)
        val existingFavourite = find(favourites, name)
        val dragDrops = ArrayList<SongDragDrop>()
        for (dragDrop in existingFavourite.dragDrops!!)
        {
            if (!songName.equals(dragDrop.title!!, ignoreCase = true))
            {
                dragDrops.add(dragDrop)
            }
        }
        existingFavourite.dragDrops = dragDrops
        val favouriteSet = HashSet<Favourite>()
        favouriteSet.add(existingFavourite)
        favouriteSet.addAll(favourites)
        sharedPreferences!!.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(ArrayList(favouriteSet))).apply()
    }

    fun setSharedPreferences(sharedPreferences: SharedPreferences)
    {
        this.sharedPreferences = sharedPreferences
    }

    fun setSongService(songService: SongService)
    {
        this.songService = songService
    }
}
