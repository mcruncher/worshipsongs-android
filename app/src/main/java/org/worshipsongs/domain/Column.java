package org.worshipsongs.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Created by madasamy on 9/8/15.
 */
public class Column implements Parcelable
{
    private String name;


    public Column(String name)
    {
        this.name = name;
    }

    public Column()
    {

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
        ToStringBuilder stringBuilder = new ToStringBuilder(this);
        stringBuilder.append("name", name);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object)
    {
       if(object instanceof Column)
       {
           Column otherObject =(Column)object;
           EqualsBuilder builder = new EqualsBuilder();
           builder.append(getName(), otherObject.getName());
           return builder.isEquals();
       }
        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getName());
        return builder.hashCode();
    }

    public static final Creator<Column> CREATOR = new Creator<Column>()
    {
        @Override
        public Column createFromParcel(Parcel in)
        {
            Column column = new Column();
            column.name = in.readString();
            return column;
        }

        @Override
        public Column[] newArray(int size)
        {
            return new Column[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
    }
}
