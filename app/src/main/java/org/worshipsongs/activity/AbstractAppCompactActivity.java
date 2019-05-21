package org.worshipsongs.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.worshipsongs.R;
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

    protected void setCustomActionBar()
    {
        if (getSupportActionBar() == null) {
            setStatusBarColor();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setBackgroundColor(getToolbarColor());
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0);
            }
            setSupportActionBar(toolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor()
    {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
        window.setStatusBarColor(typedValue.data);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private int getToolbarColor()
    {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
