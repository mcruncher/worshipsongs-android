package org.worshipsongs.domain;

import org.junit.Before;
import org.junit.Test;
import org.worshipsongs.fragment.SongYoutubeFragment;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 3.x
 */
public class DialogConfigurationTest
{
    private DialogConfiguration dialogConfiguration1;
    private DialogConfiguration dialogConfiguration2;

    @Before
    public void setUp()
    {
        dialogConfiguration1 = new DialogConfiguration("foo", "foomessage");
        dialogConfiguration2 = new DialogConfiguration(dialogConfiguration1.getTitle(), dialogConfiguration1.getMessage());
    }

    @Test
    public void testToString() throws Exception
    {
        System.out.println("--toString--");
        String result = dialogConfiguration1.toString();
        assertTrue(result.contains("foo"));
        assertFalse(result.contains("bar"));
    }

    @Test
    public void testEquals() throws Exception
    {
        System.out.println("--equals--");
        assertTrue(dialogConfiguration1.equals(dialogConfiguration2));
    }

    @Test
    public void testNotEquals()
    {
        System.out.print("--notEquals--");
        dialogConfiguration2.setTitle("title");
        assertFalse(dialogConfiguration1.equals(dialogConfiguration2));
    }

    @Test
    public void testHashCode() throws Exception
    {
        System.out.println("--hashCode--");
        Set<DialogConfiguration> sets = new HashSet<>();
        sets.add(dialogConfiguration2);
        sets.add(dialogConfiguration1);
        assertEquals(1, sets.size());
    }

}