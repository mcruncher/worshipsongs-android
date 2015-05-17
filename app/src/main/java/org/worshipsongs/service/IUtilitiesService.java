package org.worshipsongs.service;

import org.worshipsongs.domain.Verse;

import java.util.List;

/**
 * Created by Seenivasan on 5/9/2015.
 */
public interface IUtilitiesService {

    List<Verse> getVerse(String lyrics);

    List<String> getVerseByVerseOrder(String verseOrder);
}
