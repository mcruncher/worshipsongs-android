package org.worshipsongs.activity;

import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.worshipsongs.BuildConfig;
import org.worshipsongs.R;

import static org.junit.Assert.assertEquals;

/**
 * Author : Madasamy
 * Version : 3.x
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class SplashScreenActivityTest
{
    private SplashScreenActivity splashScreenActivity;

    @Before
    public void setUp()
    {
        splashScreenActivity = Robolectric.setupActivity(SplashScreenActivity.class);
        splashScreenActivity.initSetUp(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void testImageView()
    {
        ImageView imageView = (ImageView) splashScreenActivity.findViewById(R.id.imgLogo);
        assertEquals(-1, imageView.getLayoutParams().width);
        assertEquals(-1, imageView.getLayoutParams().height);
    }


}