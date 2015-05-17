package org.worshipsongs.service;

import android.util.Log;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 5/9/2015.
 */
public class UtilitiesService implements IUtilitiesService {

    private WorshipSongApplication application = new WorshipSongApplication();
    private VerseParser verseparser = new VerseParser();

    @Override
    public List<Verse> getVerse(String lyrics) {
        return verseparser.parseVerseDom(application.getContext(), lyrics);
    }

    @Override
    public List<String> getVerseByVerseOrder(String verseOrder) {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i].toLowerCase());
        }
        Log.d("Verses list: ", verses.toString());
        return verses;
    }
}
