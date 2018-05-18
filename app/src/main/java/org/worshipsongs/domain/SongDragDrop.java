package org.worshipsongs.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class SongDragDrop extends DragDrop
{

    private String tamilTitle;

    public SongDragDrop() {

    }

    public SongDragDrop(long id, String title, boolean checked)
    {
        super(id, title, checked);
    }

    public String getTamilTitle()
    {
        return tamilTitle;
    }

    public void setTamilTitle(String tamilTitle)
    {
        this.tamilTitle = tamilTitle;
    }

    @Override
    public String toString()
    {
        return super.toString() +
                "SongDragDrop{" +
                "tamilTitle='" + tamilTitle + '\'' +
                '}';
    }


    public static String toJsons(List<SongDragDrop> items)
    {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    public static List<SongDragDrop> toList(String jsonString)
    {
        if (StringUtils.isNotBlank(jsonString)) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<SongDragDrop>>()
            {
            }.getType();
            return gson.fromJson(jsonString, type);
        }
        return new ArrayList<>();
    }
}
