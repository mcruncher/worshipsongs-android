package org.worshipsongs.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class LiveShareUtilsTest
{

    @Test
    public void testFormatLiveShareUrl()
    {
        assertEquals("http://dropbox/live?id=1", LiveShareUtils.INSTANCE.formatLiveShareUrl("http://dropbox/live?id=0"));
        assertEquals("http://dropbox/live?id=1", LiveShareUtils.INSTANCE.formatLiveShareUrl("http://dropbox/live?id=1"));
    }
}
