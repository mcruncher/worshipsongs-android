package org.worshipsongs.domain;

/**
 * @Author : Madasamy
 * @Version : 1.0
 */

public class Song
{
    private int id;
    private int songBookId;
    private String title;
    private String alternateTitle;
    private String lyrics;
    private String verseOrder;
    private String copyright;
    private String comments;
    private String ccliNumber;
    private String songNumber;
    private String themeName;
    private String searchTitle;
    private String searchLyrics;
    private String createdDate;
    private String lastModified;
    private boolean temporary;

    public Song()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getSongBookId()
    {
        return songBookId;
    }

    public void setSongBookId(int songBookId)
    {
        this.songBookId = songBookId;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAlternateTitle()
    {
        return alternateTitle;
    }

    public void setAlternateTitle(String alternateTitle)
    {
        this.alternateTitle = alternateTitle;
    }

    public String getLyrics()
    {
        return lyrics;
    }

    public void setLyrics(String lyrics)
    {
        this.lyrics = lyrics;
    }

    public String getVerseOrder()
    {
        return verseOrder;
    }

    public void setVerseOrder(String verseOrder)
    {
        this.verseOrder = verseOrder;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getCcliNumber()
    {
        return ccliNumber;
    }

    public void setCcliNumber(String ccliNumber)
    {
        this.ccliNumber = ccliNumber;
    }

    public String getSongNumber()
    {
        return songNumber;
    }

    public void setSongNumber(String songNumber)
    {
        this.songNumber = songNumber;
    }

    public String getThemeName()
    {
        return themeName;
    }

    public void setThemeName(String themeName)
    {
        this.themeName = themeName;
    }

    public String getSearchTitle()
    {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle)
    {
        this.searchTitle = searchTitle;
    }

    public String getSearchLyrics()
    {
        return searchLyrics;
    }

    public void setSearchLyrics(String searchLyrics)
    {
        this.searchLyrics = searchLyrics;
    }

    public String getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(String createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(String lastModified)
    {
        this.lastModified = lastModified;
    }

    public boolean getTemporary()
    {
        return temporary;
    }

    public void setTemporary(boolean temporary)
    {
        this.temporary = temporary;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
