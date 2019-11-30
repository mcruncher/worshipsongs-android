package org.worshipsongs.service

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.support.v7.preference.PreferenceManager
import android.util.Log

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.WorshipSongApplication
import org.worshipsongs.domain.ServiceSong
import org.worshipsongs.domain.Song
import org.worshipsongs.domain.Type
import org.worshipsongs.fragment.HomeTabFragment
import org.worshipsongs.parser.ISongParser
import org.worshipsongs.parser.SongParser
import org.worshipsongs.service.DatabaseService
import org.worshipsongs.service.UserPreferenceSettingService

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator
import java.util.HashSet

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
class SongService(context: Context)
{
    private val songParser = SongParser()
    private val databaseService: DatabaseService
    private val userPreferenceSettingService: UserPreferenceSettingService
    private val sharedPreferences: SharedPreferences

    val isValidDataBase: Boolean
        get()
        {
            try
            {
                findAll()
                return true
            } catch (ex: Exception)
            {
                return false
            }

        }

    init
    {
        databaseService = DatabaseService(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        userPreferenceSettingService = UserPreferenceSettingService(context)
    }

    fun findAll(): List<Song>
    {
        val songs = ArrayList<Song>()
        val cursor = databaseService.database!!.query(TABLE_NAME, arrayOf("title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"), null, null, null, null, "title")
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val song = cursorToSong(cursor)
            songs.add(song)
            cursor.moveToNext()
        }
        cursor.close()
        return songs
    }

    fun findByTopicId(topicId: Int): List<Song>
    {
        val songs = ArrayList<Song>()
        val query = "select title,lyrics,verse_order,search_title,search_lyrics,comments, s.id " + "from songs as s inner join songs_topics as st on st.song_id = s.id inner join " + "topics as t on st.topic_id = t.id where t.id= ?"
        val cursor = databaseService.database!!.rawQuery(query, arrayOf(topicId.toString()))
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val song = cursorToSong(cursor)
            songs.add(song)
            cursor.moveToNext()
        }
        cursor.close()
        return songs
    }

    fun findByAuthorId(authorId: Int): List<Song>
    {
        val songs = ArrayList<Song>()
        val query = "select title,lyrics,verse_order,search_title,search_lyrics,comments,s.id " + "from songs as s inner join authors_songs as aus on aus.song_id = s.id inner join" + " authors as t on aus.author_id = t.id where t.id= ?"
        val cursor = databaseService.database!!.rawQuery(query, arrayOf(authorId.toString()))
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val song = cursorToSong(cursor)
            songs.add(song)
            cursor.moveToNext()
        }
        cursor.close()
        return songs
    }

    fun findBySongBookId(songBookId: Int): List<Song>
    {
        val songs = ArrayList<Song>()
        val query = "select title,lyrics,verse_order,search_title,search_lyrics,comments,s.id, entry  from " + "songs as s inner join songs_songbooks as ssb on ssb.song_id = s.id inner join " + "song_books as sb on ssb.songbook_id = sb.id where sb.id= ?"
        val cursor = databaseService.database!!.rawQuery(query, arrayOf(songBookId.toString()))
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val song = cursorToSong(cursor)
            song.songBookNumber = getSongBookNo(cursor)
            songs.add(song)
            cursor.moveToNext()
        }
        cursor.close()
        return songs
    }

    private fun getSongBookNo(cursor: Cursor): Int
    {
        try
        {
            val numberInString = cursor.getString(7)
            return Integer.parseInt(numberInString)
        } catch (ex: Exception)
        {
            return 0
        }

    }

    fun findContentsByTitle(title: String): Song?
    {
        val song = findByTitle(title)
        if (song != null)
        {
            val parsedSong = Song()
            parsedSong.title = title
            parsedSong.lyrics = song.lyrics
            parsedSong.verseOrder = song.verseOrder
            parsedSong.searchTitle = song.searchTitle
            parsedSong.searchLyrics = song.searchLyrics
            parsedSong.comments = song.comments
            parsedSong.contents = songParser.parseContents(WorshipSongApplication.context!!, song.lyrics!!, song.verseOrder!!)
            parsedSong.urlKey = songParser.parseMediaUrlKey(song.comments!!)
            parsedSong.chord = songParser.parseChord(song.comments!!)
            parsedSong.tamilTitle = songParser.parseTamilTitle(song.comments!!)
            parsedSong.id = song.id
            return parsedSong
        }
        return null
    }

    fun findByTitle(title: String): Song?
    {
        var song: Song? = null
        val whereClause = " title=\"$title\""
        val cursor = databaseService.database!!.query(TABLE_NAME, arrayOf("title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"), whereClause, null, null, null, "title")
        if (cursor.count > 0)
        {
            cursor.moveToFirst()
            song = cursorToSong(cursor)
            cursor.close()
        }
        return song
    }


    fun findById(id: Int): Song?
    {
        var song: Song? = null
        val whereClause = " id=$id"
        val cursor = databaseService.database!!.query(TABLE_NAME, arrayOf("title", "lyrics", "verse_order", "search_title", "search_lyrics", "comments", "id"), whereClause, null, null, null, "title")
        if (cursor.count > 0)
        {
            cursor.moveToFirst()
            song = cursorToSong(cursor)
            cursor.close()
        }
        return song
    }

    private fun cursorToSong(cursor: Cursor): Song
    {
        val song = Song()
        song.title = cursor.getString(0)
        song.lyrics = cursor.getString(1)
        song.verseOrder = cursor.getString(2)
        song.searchTitle = cursor.getString(3)
        song.searchLyrics = cursor.getString(4)
        song.comments = cursor.getString(5)
        song.id = cursor.getInt(6)

        song.urlKey = songParser.parseMediaUrlKey(song.comments)
        song.chord = songParser.parseChord(song.comments)
        song.tamilTitle = songParser.parseTamilTitle(song.comments)
        return song
    }

    fun count(): Long
    {
        val c = databaseService.database!!.query(TABLE_NAME, null, null, null, null, null, null)
        val result = c.count
        c.close()
        return result.toLong()
    }


    fun filterSongs(type: String, query: String, songs: List<Song>): List<Song>
    {
        val filteredSongSet = HashSet<Song>()
        if (StringUtils.isBlank(query))
        {
            filteredSongSet.addAll(songs)
        } else
        {
            for (song in songs)
            {
                if (sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true))
                {
                    if (getTitles(song.searchTitle!!).toString().toLowerCase().contains(query.toLowerCase()) || song.songBookNumber.toString().equals(query, ignoreCase = true))
                    {
                        filteredSongSet.add(song)
                    }
                } else
                {
                    if (song.searchLyrics!!.toLowerCase().contains(query.toLowerCase()))
                    {
                        filteredSongSet.add(song)
                    }
                }
                if (song.comments != null && song.comments!!.toLowerCase().contains(query.toLowerCase()))
                {
                    filteredSongSet.add(song)
                }
            }
        }
        return getSortedSongs(type, filteredSongSet)
    }

    internal fun isSearchBySongBookNumber(type: String, query: String): Boolean
    {
        val songBookNumber = getSongBookNumber(query)
        return Type.SONG_BOOK.name.equals(type, ignoreCase = true) && songBookNumber >= 0 && sharedPreferences.getBoolean(CommonConstants.SEARCH_BY_TITLE_KEY, true)
    }

    internal fun getSongBookNumber(query: String): Int
    {
        try
        {
            return Integer.parseInt(query)
        } catch (ex: Exception)
        {
            return -1
        }

    }

    fun filteredServiceSongs(query: String, serviceSongs: List<ServiceSong>?): List<ServiceSong>
    {
        val filteredServiceSongs = ArrayList<ServiceSong>()
        if (StringUtils.isBlank(query))
        {
            filteredServiceSongs.addAll(serviceSongs!!)
        } else if (serviceSongs != null && !serviceSongs.isEmpty())
        {
            for (serviceSong in serviceSongs)
            {
                if (getSearchTitles(serviceSong).toString().toLowerCase().contains(query.toLowerCase()))
                {
                    filteredServiceSongs.add(serviceSong)
                }
                if (serviceSong.song != null && serviceSong.song!!.comments != null && serviceSong.song!!.comments!!.toLowerCase().contains(query.toLowerCase()))
                {
                    filteredServiceSongs.add(serviceSong)
                }
            }
        }
        return filteredServiceSongs
    }

    internal fun getSearchTitles(serviceSong: ServiceSong?): List<String>
    {
        val searchTitles = ArrayList<String>()
        if (serviceSong != null && serviceSong.song != null && StringUtils.isNotBlank(serviceSong.song!!.searchTitle))
        {
            searchTitles.addAll(getTitles(serviceSong.song!!.searchTitle!!))
        }
        return searchTitles
    }

    internal fun getSortedSongs(type: String, filteredSongSet: Set<Song>): List<Song>
    {
        if (Type.SONG_BOOK.name.equals(type, ignoreCase = true))
        {
            val songs = ArrayList(filteredSongSet)
            Collections.sort(songs, Song.SONG_BOOK_NUMBER_ASC)
            return songs
        } else
        {
            return getSortedSongs(filteredSongSet)
        }
    }

    private fun getSortedSongs(filteredSongSet: Set<Song>): List<Song>
    {
        val tamilSongs = ArrayList<Song>()
        val englishSongs = ArrayList<Song>()
        for (song in filteredSongSet)
        {
            if (StringUtils.isNotBlank(song.tamilTitle))
            {
                tamilSongs.add(song)
            } else
            {
                englishSongs.add(song)
            }
        }
        Collections.sort(tamilSongs, SongComparator())
        Collections.sort(englishSongs, SongComparator())
        val sortedSongs = ArrayList<Song>()
        sortedSongs.addAll(tamilSongs)
        sortedSongs.addAll(englishSongs)
        return sortedSongs
    }

    fun getTitles(searchTitle: String): List<String>
    {
        return Arrays.asList(*searchTitle.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    fun getTitle(isTamil: Boolean, serviceSong: ServiceSong): String?
    {
        try
        {
            return if (isTamil && StringUtils.isNotBlank(serviceSong.song!!.tamilTitle)) serviceSong.song!!.tamilTitle
            else serviceSong.title
        } catch (ex: Exception)
        {
            return serviceSong.title
        }

    }

    private inner class SongComparator : Comparator<Song>
    {
        override fun compare(song1: Song, song2: Song): Int
        {
            if (userPreferenceSettingService.isTamil)
            {
                val result = nullSafeStringComparator(song1.tamilTitle, song2.tamilTitle)
                return if (result != 0)
                {
                    result
                } else nullSafeStringComparator(song1.title, song2.title)
            } else
            {
                return song1.title!!.compareTo(song2.title!!)
            }
        }

        private fun nullSafeStringComparator(one: String?, two: String?): Int
        {
            if (StringUtils.isBlank(one) xor StringUtils.isBlank(two))
            {
                return if (StringUtils.isBlank(one)) -1 else 1
            }
            return if (StringUtils.isBlank(one) && StringUtils.isBlank(one))
            {
                0
            } else one!!.toLowerCase().trim { it <= ' ' }.compareTo(two!!.toLowerCase().trim { it <= ' ' })
        }

    }

    companion object
    {
        val TABLE_NAME = "songs"
    }

}
