package org.worshipsongs.service;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.dao.ISongBookDao;
import org.worshipsongs.dao.SongBookDao;
import org.worshipsongs.domain.SongBook;
import org.worshipsongs.domain.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class SongBookService implements ISongBookService
{
    private ISongBookDao dao;

    public SongBookService(Context context)
    {
        dao = new SongBookDao(context);
    }

    @Override
    public List<SongBook> findAll()
    {
        return dao.findAll();
    }

    @Override
    public List<SongBook> filteredSongBooks(String query, List<SongBook> songBooks)
    {

        List<SongBook> filteredTextList = new ArrayList<SongBook>();
        if (StringUtils.isBlank(query)) {
            filteredTextList.addAll(songBooks);
        } else {
            for (SongBook songBook : songBooks) {
                if (songBook.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTextList.add(songBook);
                }
            }
        }
        return filteredTextList;

    }
}
