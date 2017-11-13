package org.worshipsongs.parser;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.utils.RegexUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class SongParser implements ISongParser
{

    private static final String CLASS_NAME = SongParser.class.getSimpleName();

    public static final String XML_FILE_NAME = "verse";
    public static final String EXTENSION = "xml";
    public static final String VERSE_ELEMENT_NAME = "verse";
    public static final String LABEL_ATTRIBUTE_NAME = "label";
    public static final String TYPE_ATTRIBUTE_NAME = "type";

    public static final String I_18_N_TITLE_REGEX = "i18nTitle.*";
    public static final String MEDIA_URL_REGEX = "mediaurl.*";
    public static final String CHORD_REGEX = "originalKey.*";


    @Override
    public List<String> parseContents(Context context, String lyrics, String verseOrder)
    {
        Log.i(CLASS_NAME, "Verse order " + verseOrder);
        ArrayList<String> contents = new ArrayList<>();
        List<Verse> verseList = parseVerse(WorshipSongApplication.getContext(), lyrics);
        Map<String, String> verseDataMap = new HashMap<String, String>();
        for (Verse verses : verseList) {
            contents.add(verses.getContent());
            verseDataMap.put(verses.getType() + verses.getLabel(), verses.getContent());
        }
        List<String> verseOrderList = getVerseOrders(verseOrder);
        if (!verseOrderList.isEmpty()) {
            contents.clear();
            for (String verseOrderKey : verseOrderList) {
                contents.add(verseDataMap.get(verseOrderKey));
            }
        }
        Log.d(this.getClass().getName(), "Parsed contents :" + contents);
        return contents;
    }

    public List<Verse> parseVerse(Context context, String lyrics)
    {
        List<Verse> verses = new ArrayList<Verse>();
        File xmlFile = null;
        try {
            Log.i(CLASS_NAME, "Preparing to parse verse " + lyrics);
            File externalCacheDir = context.getExternalCacheDir();
            xmlFile = File.createTempFile(XML_FILE_NAME, EXTENSION, externalCacheDir);
            FileUtils.write(xmlFile, lyrics);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setCoalescing(true);
            Document document = factory.newDocumentBuilder().parse(xmlFile);
            NodeList nodeList = document.getElementsByTagName(VERSE_ELEMENT_NAME);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                Verse verse = new Verse();
                verse.setLabel(Integer.parseInt(element.getAttribute(LABEL_ATTRIBUTE_NAME)));
                verse.setType(element.getAttribute(TYPE_ATTRIBUTE_NAME));
                verse.setContent(getCharacterDataFromElement(element));
                verses.add(verse);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", ex);
        } finally {
            FileUtils.deleteQuietly(xmlFile);
        }
        return verses;
    }

    String getCharacterDataFromElement(Element element)
    {
        Node child = element.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData characterData = (CharacterData) child;
            return characterData.getData();
        }
        return "";
    }

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

    public List<String> getVerseOrders(String verseOrder)
    {
        try {
            if (StringUtils.isNotBlank(verseOrder)) {
                String verseArray[] = verseOrder.split("\\s+");
                return Arrays.asList(verseArray);
            }
        } catch (Exception ex) {
            Log.i(CLASS_NAME, "Error ", ex);
        }
        return new ArrayList<>();
    }
}
