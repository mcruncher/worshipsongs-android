package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class AbstractDomain
{
    private int id;
    private String name;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Topics{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof AbstractDomain) {
            AbstractDomain otherObject = (AbstractDomain) object;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getName(), otherObject.getName());
            return builder.isEquals();
        }
        return false;

    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getName());
        return hashCodeBuilder.hashCode();
    }
}
