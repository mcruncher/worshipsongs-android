package org.worshipsongs.service;

import android.content.Context;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
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

    public AuthorService()
    {
        //Invoke only in unit test
    }

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

    public List<Author> getAuthors(String text, List<Author> authorList)
    {
        List<Author> filteredAuthors = new ArrayList<Author>();
        if (StringUtils.isNotBlank(text)) {
            for (Author author : authorList) {
                if (author.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredAuthors.add(author);
                }
            }
        } else {
            filteredAuthors.addAll(authorList);
        }
        return filteredAuthors;
    }

}
