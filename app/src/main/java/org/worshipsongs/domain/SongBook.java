package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongBook
{
    private int id;
    private String name;
    private String publisher;

    public SongBook()
    {
        this("");
    }

    public SongBook(String name)
    {
        this.name = name;
    }

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

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    @Override
    public String toString()
    {
        return "SongBook{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", publisher='" + publisher + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof SongBook) {
            SongBook otherObject = (SongBook) object;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(getName(), otherObject.getName());
            return equalsBuilder.isEquals();
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
