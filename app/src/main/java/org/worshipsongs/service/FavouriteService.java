package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.Favourite;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author : Madasamy
 * Version : 3.x.x
 */

public class FavouriteService
{

    private SharedPreferences sharedPreferences;

    public FavouriteService()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    }

    public FavouriteService(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context == null ? WorshipSongApplication.getContext() : context);
    }

    public void migration(Context context)
    {
        Log.i(FavouriteService.class.getSimpleName(), "Preparing to migrate...");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        File serviceFileName = PropertyUtils.getPropertyFile(context, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        List<String> services = PropertyUtils.getServices(serviceFileName);
        List<Favourite> favouriteList = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            String songTitleString = PropertyUtils.getProperty(services.get(i), serviceFileName);
            String songTitles[] = songTitleString.split(";");
            List<SongDragDrop> dragDrops = new ArrayList<>();
            for (int j = 0; j < songTitles.length; j++) {
                SongDragDrop songDragDrop = new SongDragDrop(j, songTitles[j], false);
                songDragDrop.setTamilTitle("");
                dragDrops.add(songDragDrop);
                Log.i(FavouriteService.class.getSimpleName(), "No. of songs " + dragDrops.size());
            }
            favouriteList.add(new Favourite(i, services.get(i), dragDrops));
        }
        Log.i(FavouriteService.class.getSimpleName(), "No. of services " + favouriteList.size());
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(favouriteList)).apply();
    }

    public void save(String serviceName, SongDragDrop songDragDrop)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.toArrays(favouriteString);
        Favourite existingFavourite = find(favourites, serviceName);
        Set<Favourite> favouriteSet = new HashSet<>();
        favouriteSet.addAll(favourites);
        if (StringUtils.isNotBlank(existingFavourite.getName())) {
            List<SongDragDrop> dragDrops = existingFavourite.getDragDrops();
            songDragDrop.setId(dragDrops.size() + 1);
            dragDrops.add(songDragDrop);
            existingFavourite.setDragDrops(dragDrops);
            favouriteSet.add(existingFavourite);
        } else {
            List<SongDragDrop> dragDrops = new ArrayList<>();
            songDragDrop.setId(0);
            dragDrops.add(songDragDrop);
            favouriteSet.add(new Favourite(favourites.size() + 1, serviceName, dragDrops));
        }
        List<Favourite> uniqueFavourites = new ArrayList<>(favouriteSet);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(uniqueFavourites)).apply();
    }

    public void save(String name, List<SongDragDrop> dragDrops)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.toArrays(favouriteString);
        Favourite existingFavourite = find(favourites, name);
        Set<Favourite> favouriteSet = new HashSet<>();
        favouriteSet.addAll(favourites);
        if (StringUtils.isNotBlank(existingFavourite.getName())) {
            existingFavourite.setDragDrops(dragDrops);
        } else {
            existingFavourite = new Favourite(name, dragDrops);
        }
        favouriteSet.add(existingFavourite);
        List<Favourite> uniqueFavourites = new ArrayList<>(favouriteSet);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(uniqueFavourites)).apply();
    }

    public Favourite find(String name)
    {
        List<Favourite> favourites = Favourite.toArrays(sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, ""));
        return find(favourites, name);
    }

    private Favourite find(List<Favourite> favourites, String favouriteName)
    {
        for (Favourite favourite : favourites) {
            if (favourite.getName().equalsIgnoreCase(favouriteName)) {
                return favourite;
            }
        }
        return new Favourite();
    }

    public List<String> findNames()
    {
        List<Favourite> favourites = Favourite.toArrays(sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, ""));
        List<String> names = new ArrayList<>();
        for (Favourite favourite : favourites) {
            names.add(favourite.getName());
        }
        return names;
    }

    public void remove(String name)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.toArrays(favouriteString);
        Favourite favourite = find(favourites, name);
        favourites.remove(favourite);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(favourites)).apply();
    }

    public void removeSong(String name, String songName)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.toArrays(favouriteString);
        Favourite existingFavourite = find(favourites, name);
        List<SongDragDrop> dragDrops = new ArrayList<>();
        for (SongDragDrop dragDrop : existingFavourite.getDragDrops()) {
            if (!songName.equalsIgnoreCase(dragDrop.getTitle())) {
                dragDrops.add(dragDrop);
            }
        }
        existingFavourite.setDragDrops(dragDrops);
        Set<Favourite> favouriteSet = new HashSet<>();
        favouriteSet.add(existingFavourite);
        favouriteSet.addAll(favourites);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.toJson(new ArrayList<>(favouriteSet))).apply();
    }

}
