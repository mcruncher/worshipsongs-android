package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.IAuthorDao;
import org.worshipsongs.domain.Author;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class AuthorService implements IAuthorService
{
    private IAuthorDao authorDao;

    public AuthorService(Context context)
    {
        authorDao = new AuthorDao(context);
    }

    public List<Author> findAll()
    {
       return authorDao.findAll();
    }

    @Override
    public String findNameByTitle(String title)
    {
        return authorDao.findAuthorNameByTitle(title);
    }

}
