package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.worshipsongs.dao.SongDao;

import java.util.List;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class SongDrag
{
    private String playlistName;
    private List<Integer> songIdList;

    public SongDrag()
    {
    }

    public SongDrag(String playlistName, List<Integer> songIdList)
    {
        this.playlistName = playlistName;
        this.songIdList = songIdList;
    }

    public String getPlaylistName()
    {
        return playlistName;
    }

    public void setPlaylistName(String playlistName)
    {
        this.playlistName = playlistName;
    }

    public List<Integer> getSongIdList()
    {
        return songIdList;
    }

    public void setSongIdList(List<Integer> songIdList)
    {
        this.songIdList = songIdList;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof SongDrag) {
            SongDrag otherObject = (SongDrag) object;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getPlaylistName(), otherObject.getPlaylistName());
            return  builder.isEquals();
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getPlaylistName());
        return builder.hashCode();
    }
}
