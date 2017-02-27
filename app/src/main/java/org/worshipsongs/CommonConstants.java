package org.worshipsongs;


import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Author: Seenivasan
 * Version: 2.1.0
 */
public final class CommonConstants
{
    public static final String COMMON_PROPERTY_TEMP_FILENAME = "common.properties";
    public static final String SERVICE_PROPERTY_TEMP_FILENAME = "service.properties";
    public static final String SERVICE_PROPERTY_CSV_FILENAME = "service.csv";
    public static final String CUSTOM_TAG_FILE_EXTENSION = "properties";
    public static final String START_TAG_PATTERN = "\\{[\\w,\\W,\\d,\\D]\\}";
    public static final String END_TAG_PATTERN = "\\{\\[\\w,\\W,\\d,\\D]\\}";


    public static final String TITLE_LIST_KEY = "titles";

    public static final String TITLE_KEY = "title";
    public static final String POSITION_KEY = "position";

    public static final String VERSION_KEY = "version";

    public static File getTagFile(File tagFile)
    {
        try {
            if (!tagFile.exists()) {
                FileUtils.touch(tagFile);
            }
        } catch (Exception ex) {
        }
        return tagFile;
    }

    //Font size
    public static final String PORTRAIT_FONT_SIZE_KEY = "portraitFontSize";
    public static final String LANDSCAPE_FONT_SIZE_KEY = "landscapeFontSize";

    public static final String DATABASE_NAME = "songs.sqlite";
    public static final String INDEX_KEY = "index";
    public static final String PROGRESS_BAR_KEY = "progressbar";
    public static final String FRAGMENT_KEY = "fragment";
    public static final String TEXTVIEW_KEY = "textview";
    public static final String REVERT_DATABASE_BUTTON_KEY = "revertDatabaseButton";
    public static final String SHOW_REVERT_DATABASE_BUTTON_KEY = "showRevertDatabaseButton";


}