package org.worshipsongs.parser

import android.content.Context

import org.worshipsongs.domain.Verse

/**
 * Author : Madasamy
 * Version : 3.x
 */

interface ISongParser
{

    fun parseContents(context: Context, lyrics: String, verseOrder: String): List<String>

    fun parseVerse(context: Context, lyrics: String): List<Verse>

    fun getVerseOrders(verseOrder: String): List<String>

    fun parseMediaUrlKey(comments: String?): String

    fun parseChord(comments: String?): String

    fun parseTamilTitle(comments: String?): String

}
