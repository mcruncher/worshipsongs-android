package org.worshipsongs.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.worshipsongs.domain.Song;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class LiveShareSongParserTest
{
    private String serviceFilePath = this.getClass().getResource("/service/service_data.osj").getFile();
    private LiveShareSongParser liveShareSongParser = new LiveShareSongParser();

    @Test
    public void testParseSong()
    {
        Song result = liveShareSongParser.parseSong(serviceFilePath, "Hand of God En Maela");
        System.out.print(result);
        assertEquals(19, result.getContents().size());
        assertEquals("Hand of God En Maela", result.getTitle());
        assertEquals("Fr. S. J. Berchmans {பெர்க்மான்ஸ்}", result.getAuthorName());
    }

    @Test
    public void testParseTitles()
    {
        List<String> result = liveShareSongParser.parseTitles(serviceFilePath);
        assertEquals(4, result.size());
        assertEquals("Hand of God En Maela", result.get(0));
        assertEquals("Azhaitheerae Yaesuvae", result.get(3));
    }

}
