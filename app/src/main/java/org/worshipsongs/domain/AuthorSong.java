package org.worshipsongs.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorSong {

    private int authorId;
    private int songId;
    private Song song;
    private Author author;


    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public Song getSong()
    {
        return song;
    }

    public void setSong(Song song)
    {
        this.song = song;
    }

    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(Author author)
    {
        this.author = author;
    }

    @Override
    public String toString()
    {
        ToStringBuilder stringBuilder = new ToStringBuilder(this);
        stringBuilder.append("authorname", getAuthor().getName());
        stringBuilder.append("song title", getSong().getTitle());
        return stringBuilder.toString();
    }
}
