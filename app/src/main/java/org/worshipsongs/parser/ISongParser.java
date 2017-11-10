package org.worshipsongs.parser;

import android.content.Context;

import org.worshipsongs.domain.Verse;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface ISongParser
{

    List<String> parseContents(Context context, String lyrics, String verseOrder);

    List<Verse> parseVerse(Context context, String lyrics);

    List<String> getVerseOrders(String verseOrder);

    String parseMediaUrlKey(String comments);

    String parseChord(String comments);

    String parseTamilTitle(String comments);

}
