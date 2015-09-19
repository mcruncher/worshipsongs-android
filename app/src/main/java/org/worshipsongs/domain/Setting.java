package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Set;

/**
 * author: madasamy
 * version: 2.1.0
 */
public class Setting
{
    private static Setting setting;

    private int position;

    private Setting()
    {

    }

    public static Setting getInstance()
    {
        if (setting == null) {
            setting = new Setting();
        }
        return setting;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    @Override
    public String toString()
    {
        ToStringBuilder stringBuilder = new ToStringBuilder(this);
        stringBuilder.append("position", position);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Setting) {
            Setting otherObject = (Setting) object;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(getPosition(), otherObject.getPosition());
            return equalsBuilder.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getPosition());
        return hashCodeBuilder.hashCode();
    }
}
