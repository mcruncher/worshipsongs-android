package org.worshipsongs.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : x.x.x
 */
public class TopicsTest
{
    private Topics topics1;
    private Topics topics2;

    @BeforeEach
    public void setUp()
    {
        topics1 = new Topics("foo");
        topics2 = new Topics(topics1.getName());
    }

    @Test
    public void testToString() throws Exception
    {
        System.out.println("--toString--");
        String result = topics1.toString();
        assertTrue(result.contains("foo"));
    }

    @Test
    public void testEquals() throws Exception
    {
        System.out.println("--equals--");
        assertTrue(topics1.equals(topics2));
    }

    @Test
    public void testNotEquals() {
        System.out.println("--notEquals--");
        topics1.setName("bar");
        assertFalse(topics1.equals(topics2));
    }

    @Test
    public void testHashCode() throws Exception
    {
        System.out.println("--hashCode--");
        Set<Topics> topicsSet = new HashSet<>();
        topicsSet.add(topics1);
        topicsSet.add(topics2);
        assertEquals(1, topicsSet.size());
    }

}