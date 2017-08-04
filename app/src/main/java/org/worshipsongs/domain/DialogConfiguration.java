package org.worshipsongs.domain;

import android.view.View;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class DialogConfiguration
{
    private String title;
    private String message = "";

    private boolean editTextVisibility;

    public DialogConfiguration()
    {
        //Do nothing
    }

    public DialogConfiguration(String title, String message)
    {
        this.title = title;
        this.message = message;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }


    public boolean isEditTextVisibility()
    {
        return editTextVisibility;
    }

    public void setEditTextVisibility(boolean editTextVisibility)
    {
        this.editTextVisibility = editTextVisibility;
    }

    @Override
    public String toString()
    {
        return "DialogConfiguration{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof DialogConfiguration) {
            DialogConfiguration otherObject = (DialogConfiguration)object;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(otherObject.getTitle(), getTitle());
            return builder.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder codeBuilder = new HashCodeBuilder();
        codeBuilder.append(getTitle());
        return codeBuilder.hashCode();
    }
}
