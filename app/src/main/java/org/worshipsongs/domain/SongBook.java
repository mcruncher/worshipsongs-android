package org.worshipsongs.domain;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */
public class SongBook
{

    private int id;
    private String name;
    private String publisher;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    @Override
    public String toString()
    {
        return  name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
