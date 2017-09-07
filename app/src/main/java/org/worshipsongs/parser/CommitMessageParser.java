package org.worshipsongs.parser;

import org.json.JSONObject;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class CommitMessageParser implements ICommitMessageParser
{
    private static final String OBJECT_KEY = "object";
    private static final String SHA_KEY = "sha";

    @Override
    public String getShaKey(String result)
    {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject object = jsonObj.getJSONObject(OBJECT_KEY);
            return object.getString(SHA_KEY);
        } catch (Exception ex) {
            return "";
        }
    }
}
