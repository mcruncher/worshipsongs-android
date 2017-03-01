package org.worshipsongs.utils;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.worshipsongs.worship.BuildConfig;

import static org.junit.Assert.*;

/**
 * Author : Madasamy
 * Version : 3.x
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class CommonUtilsTest
{
    @Test
    public void testIsProductionMode()
    {
        System.out.println("--isProductionMode--");
        assertTrue(CommonUtils.isProductionMode());
    }


    @Test
    public void testIsJellyBeanMrOrGreater()
    {
        System.out.println("--isJellyBeanMrOrGreater--");
        assertTrue(CommonUtils.isJellyBeanMrOrGreater());
    }

    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void testIsNotJellyBeanMR()
    {
        System.out.println("--isNotJellyBeanMR--");
        assertFalse(CommonUtils.isJellyBeanMrOrGreater());
    }

    @Test
    public void testIsLolliPopOrGreater()
    {
        System.out.println("--isLolliPopOrGreater--");
        assertTrue(CommonUtils.isLollipopOrGreater());
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsNotLollipop()
    {
        System.out.println("--IsNotLollipop--");
        assertFalse(CommonUtils.isLollipopOrGreater());
    }
}