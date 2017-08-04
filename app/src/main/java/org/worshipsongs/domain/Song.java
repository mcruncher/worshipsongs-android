package org.worshipsongs.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private String tamilTitle;
    private String lyrics;
    private String verse_order;
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
    private String urlKey;
    private List<String> contents;
    private String chord;
    private String authorName;

    public Song()
    {

    }

    public Song(String title)
    {
        this.title = title;
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

    public String getTamilTitle()
    {
        return tamilTitle;
    }

    public void setTamilTitle(String tamilTitle)
    {
        this.tamilTitle = tamilTitle;
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
        return verse_order;
    }

    public void setVerseOrder(String verseOrder)
    {
        this.verse_order = verseOrder;
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

    public String getUrlKey()
    {
        return urlKey;
    }

    public void setUrlKey(String urlKey)
    {
        this.urlKey = urlKey;
    }

    public List<String> getContents()
    {
        return contents;
    }

    public String getChord()
    {
        return chord;
    }

    public void setChord(String chord)
    {
        this.chord = chord;
    }

    public void setContents(List<String> contents)
    {
        this.contents = contents;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    @Override
    public String toString()
    {
        ToStringBuilder stringBuilder = new ToStringBuilder(this);
        stringBuilder.append("title", getTitle());
        stringBuilder.append("verse order", getVerseOrder());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Song) {
            Song otherObject = (Song) object;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(getTitle(), otherObject.getTitle());
            equalsBuilder.append(getSearchTitle(), otherObject.getSearchTitle());
            return equalsBuilder.isEquals();
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getTitle());
        hashCodeBuilder.append(getSearchTitle());
        return hashCodeBuilder.hashCode();
    }
}
