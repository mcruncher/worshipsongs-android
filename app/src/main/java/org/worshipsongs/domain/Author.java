package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class Author
{
    private int id;
    private String firstName;
    private String lastName;
    private String name;
    private String tamilName;
    private String defaultName;
    private int noOfSongs;

    public Author()
    {

    }
    public Author(String name)
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

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
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
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append(super.toString());
        builder.append("firstname", firstName);
        builder.append("lastName", lastName);
        return builder.toString();
    }
}
