package org.worshipsongs.service;



import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;

/**
 * @author Madasamy
 * @since
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
