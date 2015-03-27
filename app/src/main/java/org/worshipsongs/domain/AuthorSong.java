package org.worshipsongs.domain;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorSong {

    private int authorId;
    private int songId;


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
}
