package org.worshipsongs.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.domain.Verse;

import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class SongParserIntegrationTest {
    private SongParser songParser = new SongParser();
    private String lyrics = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            " <song version=\"1.0\"><lyrics><verse label=\"1\" type=\"c\"><![CDATA[{y}அக்கினி அபிஷேகம்{/y}\n" +
            " Akkini abishegam\n" +
            " {y}பொழிந்திடுவீர் தேவா{/y}\n" +
            " Pozhinthiduveer Thevaa]]></verse><verse label=\"2\" type=\"c\"><![CDATA[{y}காத்திருக்கும் அடியாரை{/y}\n" +
            " Kaathirukkum adiyaarai\n" +
            " {y}நிரப்புவீர் இக்கணமே{/y}\n" +
            " Nirappuveer ikkanamae]]></verse><verse label=\"1\" type=\"v\"><![CDATA[{y}1. ஆவியை நீர் ஊற்றிடுவீர்{/y}\n" +
            " Aaviyai Neer ootriduveer\n" +
            " {y}உயிரடைந்திடவே செய்திடுவீர்{/y}\n" +
            " Uyiradainthidavae seithiduveer]]></verse><verse label=\"1\" type=\"o\"><![CDATA[{y}புது பெலன் ஜீவன் தந்திடுவீர்{/y}\n" +
            " Puthu belan jeevan thanthiduveer\n" +
            " {y}சாட்சியாக வாழ்ந்திடவே{/y}\n" +
            " Saatchiyaaga vaazhnthidavae]]></verse><verse label=\"2\" type=\"v\"><![CDATA[{y}2. இரக்கமாக அக்கினியை{/y}\n" +
            " Irakkamaaga akkiniyai\n" +
            " {y}உருக்கமாய் உள்ளத்தில் ஊற்றிடுமே{/y}\n" +
            " Urukkamaai ullathil ootridumae]]></verse><verse label=\"2\" type=\"o\"><![CDATA[{y}பரிசுத்த பாதையில் நடத்திடுவீர்{/y}\n" +
            " Parisutha paathaiyil nadathiduveer\n" +
            " {y}கிருபையிலே வளர்ந்திடவே{/y}\n" +
            " Kirubaiyilae valarnthidavae]]></verse><verse label=\"3\" type=\"v\"><![CDATA[{y}3. மாம்சமான யாவர் மேலும்{/y}\n" +
            " Maamsaamaana yaavar maelum\n" +
            " {y}ஆவியின் அனலை மூட்டிடுவீர்{/y}\n" +
            " Aaaviyin analai mootiduveer]]></verse><verse label=\"3\" type=\"o\"><![CDATA[{y}வானம் திறந்து வந்திடுவீர்{/y}\n" +
            " Vaanam thiranthu vanthiduveer\n" +
            " {y}வல்லமையை ஈந்திடுவீர்{/y}\n" +
            " Vallamaiyai eenthiduveer]]></verse></lyrics></song>";

    @Test
    public void parseContentsByVerseOrder() {
        // given:
        String verseOrder = "c1 c2 v1 o1 c1 c2 v2 o2 c1 c2 v3 o3";

        // when:
        List<String> result = songParser.parseContents(RuntimeEnvironment.application.getApplicationContext(), lyrics, verseOrder);

        // then:
        assertEquals(12, result.size());
    }

    @Test
    public void parseContentsWhenVerseOrderIsEmpty() {
        // given:
        String verseOrder = "";

        // when:
        List<String> result = songParser.parseContents(RuntimeEnvironment.application.getApplicationContext(), lyrics, verseOrder);

        // then:
        assertEquals(8, result.size());
    }

    @Test
    public void parseVerse() {
        // when:
        List<Verse> result = songParser.parseVerse(RuntimeEnvironment.application.getApplicationContext(), lyrics);

        // then:
        assertEquals(8, result.size());
    }

    @Test
    public void parseVerseFromEmptyString() {
        // expect:
        assertEquals(0, songParser.parseVerse(RuntimeEnvironment.application.getApplicationContext(), "").size());
    }
}
