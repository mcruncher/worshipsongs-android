package org.worshipsongs.parser;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public interface ISongParser
{
    String parseMediaUrlKey(String comments);

    String parseChord(String comments);

    String parseTamilTitle(String comments);
}
