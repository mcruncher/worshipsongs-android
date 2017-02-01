package org.worshipsongs.dialog;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;

import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : 2.x
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DefaultRemotePresentation extends Presentation
{

    public DefaultRemotePresentation(Context context, Display display)
    {
        super(context, display);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

    }
}
