package org.worshipsongs.parser;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class CommitMessageParserTest
{

    private ICommitMessageParser commitMessageParser = new CommitMessageParser();
    String json = "{\"ref\":\"refs/heads/master\"," +
            "\"url\":\"https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/refs/heads/master\"," +
            "\"object\":{\"sha\":\"f80df6c8de7020a5f8a2b984117952bbadd90259\"," +
            "\"type\":\"commit\"," +
            "\"url\":\"https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/commits/f80df6c8de7020a5f8a2b984117952bbadd90259\"}}";

    @Test
    public void testGetShaKey() throws Exception
    {
        System.out.println("getShaKey");
        String shaKey = commitMessageParser.getShaKey(json);
        assertEquals("f80df6c8de7020a5f8a2b984117952bbadd90259", shaKey);
    }

    @Test
    public void testGetShaKeyFromNull()
    {
        System.out.println("getShaKeyFromNull");
        String shaKey = commitMessageParser.getShaKey(null);
        assertEquals("", shaKey);
    }

    @Test
    public void testGetShaKeyFromEmptyString()
    {
        System.out.println("getShaKeyFromEmptyString");
        String shaKey = commitMessageParser.getShaKey("");
        assertEquals("", shaKey);
    }


}