package org.worshipsongs.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

/**
 * Author : Madasamy
 * Version : 4.x.x
 */

public class DragDrop
{
    private long id;
    private String title;
    private boolean checked;

    public DragDrop(long id, String title, boolean checked)
    {
        this.id = id;
        this.title = title;
        this.checked = checked;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    @Override
    public String toString()
    {
        return "DragDrop{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", checked=" + checked +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof DragDrop) {
            DragDrop otherObject = (DragDrop) object;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getTitle(), otherObject.getTitle());
            return builder.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getTitle());
        return hashCodeBuilder.hashCode();
    }

    public static String toJson(ArrayList<DragDrop> items)
    {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    public static ArrayList<DragDrop> toArrays(String jsonString)
    {
        if (StringUtils.isNotBlank(jsonString)) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<DragDrop>>()
            {
            }.getType();
            return gson.fromJson(jsonString, type);
        }
        return new ArrayList<>();
    }

}
