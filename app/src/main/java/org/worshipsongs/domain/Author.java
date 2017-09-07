package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class Author extends AbstractDomain
{

    private String firstName;
    private String lastName;
    private String tamilName;
    private String defaultName;

    public Author()
    {

    }

    public Author(String name)
    {
        setName(name);
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
