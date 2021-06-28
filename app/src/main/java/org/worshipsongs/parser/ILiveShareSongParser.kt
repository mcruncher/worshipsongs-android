package org.worshipsongs.parser

import org.worshipsongs.domain.Song

interface ILiveShareSongParser
{
    fun parseSong(serviceFilePath: String, songTitle: String): Song

    fun parseServices(serviceDirPath: String): MutableList<String>

    fun parseTitles(serviceFilePath: String): MutableList<String>
}