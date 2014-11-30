package org.worshipsongs;


import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by Seenivasan on 10/26/2014.
 */
public final class CommonConstants {
    public static final String COMMON_PROPERTY_TEMP_FILENAME ="common.properties";
    public static final String CUSTOM_TAG_FILE_EXTENSION="properties";
    public static final String START_TAG_PATTERN = "\\{[\\w,\\W,\\d,\\D]\\}";
    public static final String END_TAG_PATTERN  = "\\{\\[\\w,\\W,\\d,\\D]\\}";

    public static File getTagFile(File tagFile) {
        try{
            if(!tagFile.exists()){
                FileUtils.touch(tagFile);
            }
        }
        catch (Exception ex){

        }
    return tagFile;
    }

}
