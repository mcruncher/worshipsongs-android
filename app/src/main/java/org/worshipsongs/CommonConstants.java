package org.worshipsongs;


import org.apache.commons.lang3.StringUtils;

/**
 * Author: Seenivasan
 * Version: 2.1.0
 */
public final class CommonConstants
{
    public static final String COMMON_PROPERTY_TEMP_FILENAME = "common.properties";
    public static final String SERVICE_PROPERTY_TEMP_FILENAME = "service.properties";

    public static final String TITLE_LIST_KEY = "titles";
    public static final String TITLE_KEY = "title";
    public static final String LOCALISED_TITLE_KEY = "localisedTitle";
    public static final String SUBTITLE_KEY = "subTitle";
    public static final String PLAY_IMAGE_KEy = "playImage";
    public static final String OPTIONS_IMAGE_KEY = "optionsImage";
    public static final String COUNT_KEY = "count";
    public static final String MESSAGE_KEY = "message";
    public static final String POSITION_KEY = "position";
    public static final String VERSION_KEY = "version";

    public static final String DATABASE_NAME = "songs.sqlite";
    public static final String INDEX_KEY = "index";
    public static final String PROGRESS_BAR_KEY = "progressbar";
    public static final String TEXTVIEW_KEY = "textview";
    public static final String REVERT_DATABASE_BUTTON_KEY = "revertDatabaseButton";
    public static final String SHOW_REVERT_DATABASE_BUTTON_KEY = "showRevertDatabaseButton";
    public static final String PRIMARY_FONT_SIZE_KEY = "portraitFontSize";
    public static final String PRESENTATION_FONT_SIZE_KEY = "landscapeFontSize";
    public static final String REMOTE_URL = "remoteUrl";
    public static final String SEARCH_BY_TITLE_KEY = "searchByTitle";

    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String LANGUAGE_INDEX_KEY = "languageIndex";
    public static final String COMMIT_SHA_KEY = "commitShaKey";
    public static final String STATE_KEY = "listViewState";
    public static final String NAME_KEY = "nameKey";
    public static final String SERVICE_NAME_KEY = "serviceNameKey";
    public static final String UPDATED_SONGS_KEY = "updatedSongsKey";

    public static final String NO_OF_SONGS = "noOfSongs";

    public static final String LANGUAGE_CHOOSED_KEY = "languageChoosedKey";
    public static final String SONG_BOOK_NUMBER_KEY = "songBookNumberKey";
    public static final String TAB_CHOICE_KEY = "tabChoicesKey";
    public static final String UPDATE_NAV_ACTIVITY_KEY = "updateNavActivityKey";

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 1001;

    //Favourites
    public static final String IMPORTED_SONGS_KEY = "favouritesKey";
    public static final String MIGRATION_KEY = "migrationKey";
    public static final String DISPLAY_FAVOURITE_HELP_ACTIVITY = "displayFavouriteHelpActivity";

    private CommonConstants()
    {
        //Do nothing
    }

}