package org.worshipsongs.service;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;

/**
 * Author : Madasamy
 * Version : x.x.x
 */
@RunWith(AndroidJUnit4.class)
public class TopicServiceIntegrationTest
{
    private TopicService topicService;

    @Before
  public  void setUp() throws Exception
    {
        topicService = new TopicService(getTargetContext());
    }

    @Test
   public void testFindAll()
    {
        System.out.println("--findAll--");
        assertEquals(1, topicService.findAll().size());
    }
}
