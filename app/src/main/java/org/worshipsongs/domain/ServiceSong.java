package org.worshipsongs.domain;

/**
 * @author Vignesh Palanisamy
 * @version 3.x
 */

public class ServiceSong
{
    private String title;
    private Song song;

    public ServiceSong(String title, Song song)
    {
        this.title = title;
        this.song = song;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Song getSong()
    {
        return song;
    }

    public void setSong(Song song)
    {
        this.song = song;
    }
}
