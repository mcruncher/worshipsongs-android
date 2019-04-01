package org.worshipsongs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.worshipsongs.utils.ThemeUtils;

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
public class AbstractAppCompactActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
    }

    public void reCreate()
    {
        Bundle savedInstanceState = new Bundle();
        onSaveInstanceState(savedInstanceState);
        super.onDestroy();
        onCreate(savedInstanceState);
    }
}
