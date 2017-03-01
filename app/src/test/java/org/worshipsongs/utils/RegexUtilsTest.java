package org.worshipsongs.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: Madasamy
 * @version: 2.4.0
 */
public class RegexUtilsTest
{
    @Test
    public void testGetMatchString_1()
    {
        System.out.println("--getMatchString_1--");
        String line = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n" +
                "bar=foo";
        assertEquals("mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI", RegexUtils.getMatchString(line, "mediaurl" + ".*"));
    }

    @Test
    public void testGetMatchString_2()
    {
        System.out.println("--getMatchString_2--");
        String line = "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n" +
                "chord=g";
        assertEquals("chord=g", RegexUtils.getMatchString(line, "chord" + ".*"));
    }

    @Test
    public void testGetMatchString_3()
    {
        System.out.println("--getMatchString_3--");
        String line = "chord=g\n" +
                "mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI\n";
        assertEquals("chord=g", RegexUtils.getMatchString(line, "chord" + ".*"));
        assertEquals("mediaUrl=https://www.youtube.com/watch?v=Ro59iCBNBdI", RegexUtils.getMatchString(line, "mediaurl" + ".*"));
    }
}