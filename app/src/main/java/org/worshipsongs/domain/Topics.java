package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class Topics extends AbstractDomain
{

    private String tamilName;
    private String defaultName;

    public Topics()
    {
        //Do nothing
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

}
