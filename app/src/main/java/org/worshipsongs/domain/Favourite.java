package org.worshipsongs.domain;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class Favourite implements Comparable
{
    private int orderId;
    private String name;
    private List<SongDragDrop> dragDrops;

    public Favourite()
    {

    }

    public Favourite(String name, List<SongDragDrop> dragDrop)
    {
        this(0, name, dragDrop);
    }

    public Favourite(int orderId, String name, List<SongDragDrop> dragDrops)
    {
        this.orderId = orderId;
        this.name = name;
        this.dragDrops = dragDrops;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<SongDragDrop> getDragDrops()
    {
        return dragDrops;
    }

    public void setDragDrops(List<SongDragDrop> dragDrops)
    {
        this.dragDrops = dragDrops;
    }

    @Override
    public String toString()
    {
        return "Favourite{" +
                "name='" + name + '\'' +
                ", dragDrops=" + dragDrops +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Favourite) {
            Favourite otherObject = (Favourite) object;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(otherObject.getName(), getName());
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

    public static String toJson(List<Favourite> items)
    {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    public static List<Favourite> toArrays(String jsonString)
    {
        if (StringUtils.isNotBlank(jsonString)) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Favourite>>()
            {
            }.getType();
            return gson.fromJson(jsonString, type);
        }
        return new ArrayList<>();
    }

    @Override
    public int compareTo(@NonNull Object object)
    {
        if (object instanceof Favourite) {
            Favourite rhs = (Favourite) object;
            return rhs.getOrderId() < getOrderId() ? -1 : getOrderId() == rhs.getOrderId() ? 0 : 1;
        }
        return 0;
    }
}
