package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class Topics extends AbstractDomain
{

    public Topics()
    {
    }

    public Topics(String name)
    {
        this.setName(name);
    }

}
