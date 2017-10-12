package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class Topics
{
    private int id;
    private String name;
    private String tamilName;
    private String defaultName;
    private int noOfSongs;

    public Topics()
    {
        //Do nothing
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

    public String getTamilName()
    {
        return tamilName;
    }

    public void setTamilName(String tamilName)
    {
        this.tamilName = tamilName;
    }

    public String getDefaultName()
    {
        return defaultName;
    }

    public void setDefaultName(String defaultName)
    {
        this.defaultName = defaultName;
    }

    public Topics(String name)
    {
        this.setName(name);
    }

    public int getNoOfSongs()
    {
        return noOfSongs;
    }

    public void setNoOfSongs(int noOfSongs)
    {
        this.noOfSongs = noOfSongs;
    }

    @Override
    public String toString()
    {
        return "Topics{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tamilName='" + tamilName + '\'' +
                ", defaultName='" + defaultName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Topics) {
            Topics otherObject = (Topics) object;
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
