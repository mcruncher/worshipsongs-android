package org.worshipsongs.parser

import org.worshipsongs.domain.Song

interface ILiveShareSongParser
{
    fun parseSong(serviceFilePath: String, songTitle: String): Song

    fun parseTitles(serviceFilePath: String): MutableList<String>
}