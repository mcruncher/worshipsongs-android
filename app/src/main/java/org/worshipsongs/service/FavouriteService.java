package org.worshipsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.Favourite;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.SongDragDrop;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private SongService songService;

    public FavouriteService()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
        songService = new SongService(WorshipSongApplication.getContext());
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
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.Companion.toJson(favouriteList)).apply();
    }

    public void save(String serviceName, SongDragDrop songDragDrop)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.Companion.toArrays(favouriteString);
        Favourite existingFavourite = find(favourites, serviceName);
        Set<Favourite> favouriteSet = new HashSet<>();
        favouriteSet.addAll(favourites);
        if (StringUtils.isNotBlank(existingFavourite.getName())) {
            List<SongDragDrop> dragDrops = existingFavourite.getDragDrops();
            dragDrops.add(songDragDrop);
            existingFavourite.setDragDrops(dragDrops);
            favouriteSet.add(existingFavourite);
        } else {
            List<SongDragDrop> dragDrops = new ArrayList<>();
            dragDrops.add(songDragDrop);
            favouriteSet.add(new Favourite(getFavouritesNewOrderNumber(favourites), serviceName, dragDrops));
        }
        List<Favourite> uniqueFavourites = new ArrayList<>(favouriteSet);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.Companion.toJson(uniqueFavourites)).apply();
    }

    public int getFavouritesNewOrderNumber(List<Favourite> favourites)
    {
        if (favourites.isEmpty()) {
            return 0;
        } else {
            Collections.sort(favourites);
            return favourites.get(0).getOrderId() + 1;
        }
    }

    public void save(String name, List<SongDragDrop> dragDrops)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.Companion.toArrays(favouriteString);
        Favourite existingFavourite = find(favourites, name);
        Set<Favourite> favouriteSet = new HashSet<>();
        favouriteSet.addAll(favourites);
        if (StringUtils.isNotBlank(existingFavourite.getName())) {
            existingFavourite.setDragDrops(dragDrops);
        } else {
            existingFavourite = new Favourite(getFavouritesNewOrderNumber(favourites), name, dragDrops);
        }
        favouriteSet.add(existingFavourite);
        List<Favourite> uniqueFavourites = new ArrayList<>(favouriteSet);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.Companion.toJson(uniqueFavourites)).apply();
    }

    public Favourite find(String name)
    {
        List<Favourite> favourites = Favourite.Companion.toArrays(sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, ""));
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
        List<Favourite> favourites = Favourite.Companion.toArrays(sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, ""));
        Collections.sort(favourites);
        List<String> names = new ArrayList<>();
        for (Favourite favourite : favourites) {
            names.add(favourite.getName());
        }
        return names;
    }

    public String buildShareFavouriteFormat(String name)
    {
        Favourite favourite = find(name);
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append(name).append(";");
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("\n\n");
        List<SongDragDrop> dragDrops = favourite.getDragDrops();
        for (int i = 0; i < dragDrops.size(); i++) {
            SongDragDrop songDragDrop = dragDrops.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(StringUtils.isNotBlank(songDragDrop.getTamilTitle()) ?
                            songDragDrop.getTamilTitle() + "\n" : "")
                    .append(songDragDrop.getTitle())
                    .append("\n\n");
            if (songDragDrop.getId() > 0) {
                linkBuilder.append(songDragDrop.getId()).append(";");
            } else {
                Song song = songService.findByTitle(songDragDrop.getTitle());
                linkBuilder.append(song.getId()).append(";");
            }
        }
        String base64String = Base64.encodeToString(linkBuilder.toString().getBytes(), 0);
        builder.append("\n").append("https://mcruncher.github.io/worshipsongs/?").append(base64String);
        return builder.toString();
    }

    public List<Song> findSongsByFavouriteName(String name)
    {
        List<Song> songs = new ArrayList<>();
        Favourite favourite = find(name);
        for (SongDragDrop songDragDrop : favourite.getDragDrops()) {
            Song song = songService.findContentsByTitle(songDragDrop.getTitle());
            if (song != null) {
                songs.add(song);
            }
        }
        return songs;
    }

    public void remove(String name)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.Companion.toArrays(favouriteString);
        Favourite favourite = find(favourites, name);
        favourites.remove(favourite);
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.Companion.toJson(favourites)).apply();
    }

    public void removeSong(String name, String songName)
    {
        String favouriteString = sharedPreferences.getString(CommonConstants.FAVOURITES_KEY, "");
        List<Favourite> favourites = Favourite.Companion.toArrays(favouriteString);
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
        sharedPreferences.edit().putString(CommonConstants.FAVOURITES_KEY, Favourite.Companion.toJson(new ArrayList<>(favouriteSet))).apply();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    public void setSongService(SongService songService)
    {
        this.songService = songService;
    }
}
