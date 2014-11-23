package org.worshipsongs.parser;

import android.content.Context;
import android.util.Log;


import org.apache.commons.io.FileUtils;
import org.worshipsongs.handler.VerseHandler;
import org.worshipsongs.domain.Verse;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class VerseParser
{
    public static final String XML_FILE_NAME = "verse";
    public static final String EXTENSION = "xml";
    public static final String VERSE_ELEMENT_NAME ="verse";
    public static final String LABEL_ATTRIBUTE_NAME = "label";
    public static final String TYPE_ATTRIBUTE_NAME = "type";
    private File xmlFile = null;

    public List<Verse> parseVerse(Context context, String content)
    {
        List<Verse> verses = new ArrayList<Verse>();
        try {
            File externalCacheDir = context.getExternalCacheDir();
            xmlFile = File.createTempFile(XML_FILE_NAME, EXTENSION, externalCacheDir);
            Log.d(this.getClass().getName(), xmlFile.getAbsolutePath() + "created successfully");
            FileUtils.write(xmlFile, content);
            Log.d(this.getClass().getName(), "XML Content" + FileUtils.readFileToString(xmlFile));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            VerseHandler verseHandler = new VerseHandler();
            parser.parse(xmlFile, verseHandler);
            verses.addAll(verseHandler.getVerseList());
            return verses;
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", ex);
        } finally {
            xmlFile.deleteOnExit();
        }
        return verses;
    }

    public List<Verse> parseVerseDom(Context context, String content)
    {
        List<Verse> verses = new ArrayList<Verse>();
        try {
            File externalCacheDir = context.getExternalCacheDir();
            xmlFile = File.createTempFile(XML_FILE_NAME, EXTENSION, externalCacheDir);
            Log.d(this.getClass().getName(), xmlFile.getAbsolutePath() + "created successfully");
            FileUtils.write(xmlFile, content);
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
            xmlFile.deleteOnExit();
        }
        return verses;
    }

    public static String getCharacterDataFromElement(Element element)
    {
        Node child = element.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData characterData = (CharacterData) child;
            return characterData.getData();
        }
        return "";
    }
}
