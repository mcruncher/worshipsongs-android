package org.worshipsongs.parser;

import org.worshipsongs.utils.RegexUtils;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongParser implements ISongParser
{

    public static final String I_18_N_TITLE_REGEX = "i18nTitle.*";
    public static final String MEDIA_URL_REGEX = "mediaurl.*";
    public static final String CHORD_REGEX = "originalKey.*";

    public String parseMediaUrlKey(String comments)
    {
        String mediaUrl = "";
        if (comments != null && comments.length() > 0) {
            String mediaUrlLine = RegexUtils.getMatchString(comments, MEDIA_URL_REGEX);
            String[] medialUrlArray = mediaUrlLine.split("=");
            if (medialUrlArray != null && medialUrlArray.length >= 3) {
                mediaUrl = medialUrlArray[2];
            }
        }
        return mediaUrl;
    }

    public String parseChord(String comments)
    {
        String chord = "";
        if (comments != null && comments.length() > 0) {
            String chordLine = RegexUtils.getMatchString(comments, CHORD_REGEX);
            String[] chordArray = chordLine.split("=");
            if (chordArray != null && chordArray.length >= 2) {
                chord = chordArray[1];
            }
        }
        return chord;
    }

    @Override
    public String parseTamilTitle(String comments)
    {
        String tamilTitle = "";
        if (comments != null && comments.length() > 0) {
            String tamilTitleLine = RegexUtils.getMatchString(comments, I_18_N_TITLE_REGEX);
            String[] chordArray = tamilTitleLine.split("=");
            if (chordArray != null && chordArray.length >= 2) {
                tamilTitle = chordArray[1];
            }
        }
        return tamilTitle;
    }
}
