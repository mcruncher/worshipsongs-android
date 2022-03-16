package org.worshipsongs.service

import android.content.Context
import android.database.Cursor
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.domain.Author
import org.worshipsongs.domain.AuthorSong
import org.worshipsongs.domain.Song
import java.util.*

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

class AuthorService(context: Context)
{
    private val allColumns = arrayOf(COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_DISPLAY_NAME)
    private val columns = arrayOf(COLUMN_AUTHOR_ID)
    private val databaseService: DatabaseService

    init
    {
        databaseService = DatabaseService(context)
    }

    fun findAll(): List<Author>
    {
        val authors = ArrayList<Author>()
        val query = "select t.id, t.first_name, t.last_name, t.display_name, count(t.id) " + "from songs as s inner join authors_songs as aus on aus.song_id = s.id inner join " + "authors as t on aus.author_id = t.id group by t.first_name ORDER by t.display_name"
        val cursor = databaseService.get().rawQuery(query, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val author = cursorToAuthor(cursor)
            authors.add(author)
            cursor.moveToNext()
        }
        cursor.close()
        return authors
    }

    private fun cursorToAuthor(cursor: Cursor): Author
    {
        val author = Author()
        author.id = cursor.getInt(0)
        author.firstName = cursor.getString(1)
        author.lastName = cursor.getString(2)
        author.name = cursor.getString(3)
        author.noOfSongs = cursor.getInt(4)
        author.tamilName = databaseService.parseTamilName(author.name!!)
        author.defaultName = databaseService.parseEnglishName(author.name!!)
        return author
    }

    fun findAuthorNameByTitle(title: String): String?
    {
        val query = "select title, lyrics, verse_order, first_name, last_name, display_name" + " from songs as song , authors as author, authors_songs as authorsong where " + "song.id = authorsong.song_id and author.id = authorsong.author_id and song.title = ?"
        val cursor = databaseService.get().rawQuery(query, arrayOf(title))
        cursor.moveToFirst()
        val authorSong = getAuthorSong(cursor)
        return authorSong.author!!.name
    }

    private fun getAuthorSong(cursor: Cursor): AuthorSong
    {
        val song = Song()
        song.title = cursor.getString(0)
        song.lyrics = cursor.getString(1)
        song.verseOrder = cursor.getString(2)

        val author = Author()
        author.firstName = cursor.getString(3)
        author.lastName = cursor.getString(4)
        author.name = cursor.getString(5)
        author.tamilName = databaseService.parseTamilName(author.name!!)
        author.defaultName = databaseService.parseEnglishName(author.name!!)

        val authorSong = AuthorSong()
        authorSong.song = song
        authorSong.author = author
        return authorSong
    }

    fun getAuthors(text: String, authorList: List<Author>): List<Author>
    {
        val filteredAuthors = ArrayList<Author>()
        if (StringUtils.isNotBlank(text))
        {
            for (author in authorList)
            {
                if (author.name!!.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredAuthors.add(author)
                }
            }
        } else
        {
            filteredAuthors.addAll(authorList)
        }
        return filteredAuthors
    }

    fun findAuthorsByTitle(title: String?): List<String>
    {
        val authors = ArrayList<String>()
        val query = "select a.display_name from Songs as s, authors as a, authors_songs as ass where s.id = ass.song_id and ass.author_id = a.id and s.title = ?"
        val cursor = databaseService.get().rawQuery(query,  arrayOf(title))
        cursor.moveToFirst()
        while (!cursor.isAfterLast)
        {
            val author = cursor.getString(0)
            authors.add(author)
            cursor.moveToNext()
        }
        cursor.close()
        return authors
    }

    companion object
    {
        val TABLE_NAME_AUTHOR = "authors"
        val COLUMN_ID = "id"
        val COLUMN_FIRST_NAME = "first_name"
        val COLUMN_LAST_NAME = "last_name"
        val COLUMN_DISPLAY_NAME = "display_name"
        val COLUMN_AUTHOR_ID = "author_id"
        val AUTHOR_NAME_REGEX = "\\{.*\\}"
    }


}
