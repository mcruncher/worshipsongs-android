package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class Verse implements Serializable
{
    private String type;
    private int label;
    private String content;
    private String verseOrder;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getLabel()
    {
        return label;
    }

    public void setLabel(int label)
    {
        this.label = label;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getVerseOrder()
    {
        return content;
    }

    public void setVerseOrder(String content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
