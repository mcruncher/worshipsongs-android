package org.worshipsongs.locator;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public interface IImportDatabaseLocator
{
    void load(AppCompatActivity appCompatActivity, Map<String, Object> objects);

}
