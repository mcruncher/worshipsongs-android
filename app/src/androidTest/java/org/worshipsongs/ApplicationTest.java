//package org.worshipsongs;
//
//import android.test.ActivityInstrumentationTestCase2;
//import android.view.View;
//import android.widget.TextView;
//
//import com.jayway.android.robotium.solo.Solo;
//
//import org.worshipsongs.activity.SplashScreenActivity;
//import org.worshipsongs.worship.R;
//
///**
// * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
// */
//
//public class ApplicationTest extends ActivityInstrumentationTestCase2
//{
//    private Solo solo;
//
//    public ApplicationTest()
//    {
//        super("org.worshipsongs.activity", SplashScreenActivity.class);
//    }
//
//    @Override
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//        solo = new Solo(getInstrumentation(), getActivity());
//    }
//
//    @Override
//    protected void tearDown() throws Exception
//    {
//        try
//        {
//            solo.finalize();
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//        }
//        getActivity().finish();
//        super.tearDown();
//    }
//
//    public void testSample() throws InterruptedException
//    {
//        TextView alert = (TextView)getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        assertEquals("Checking for database updates...", alert.getText());
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.sendKey(Solo.MENU);
//        solo.clickOnText("About");
//
//        solo.waitForActivity("org.worshipsongs.activity.AboutWebViewActivity", 3000);
//        solo.goBack();
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A");
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnText("Font Size");
//
//        solo.clickOnText("NORMAL");
//
//        solo.clickOnText("Cancel");
//    }
//
//    public void testSplashScreenActivity()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        assertEquals("Checking for database updates...", alert.getText());
//
//        testSongsList();
//    }
//
//    public void testSongsList()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//    }
///*
//    public void testAboutWebActivity() throws InterruptedException
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//        //  solo.assertCurrentActivity("Songs List View", SongsListActivity.class);
//
//        solo.sendKey(Solo.MENU);
//
//        solo.clickOnText("About");
//
//        solo.waitForActivity("org.worshipsongs.activity.AboutWebViewActivity", 3000);
//
//        //assertTrue(solo.searchText("About"));
//
//        assertEquals("String Found", true, solo.searchText("About"));
//
//        solo.goBack();
//    }
//*/
//    public void testSearchSong()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        assertEquals("String Found", true, solo.searchText("A New Commandment"));
//    }
//
//    public void testClickSong()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        assertEquals("String Found", true, solo.searchText("A New commandment"));
//    }
//
//    public void testFontSize()
//    {
//        //solo.goBack();
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnText("Font Size");
//
//        solo.clickOnText("MEDIUM");
//
//        assertEquals("String Found", true, solo.searchText("MEDIUM"));
//    }
//
//    public void testFontStyle()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnText("Font Style");
//
//        solo.clickOnText("ITALIC");
//
//        assertEquals("String Found", true, solo.searchText("ITALIC"));
//    }
//
//    public void testFontFace()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnText("Font Face");
//
//        solo.clickOnText("SERIF");
//
//        assertEquals("String Found", true, solo.searchText("SERIF"));
//    }
//
//    public void testAwakeKeepScreen()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnCheckBox(0);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        assertEquals("String Found", true, solo.isCheckBoxChecked(0));
//    }
//
//    public void testResetDefault()
//    {
//        TextView alert = (TextView) getActivity().findViewById(R.id.message);
//
//        solo.waitForText("Checking for database updates");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        View search = solo.getView(R.id.action_search);
//        solo.clickOnView(search);
//
//        solo.enterText(0, "A New Commandment");
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsListActivity",3000);
//
//        solo.clickInList(1);
//
//        solo.waitForActivity("org.worshipsongs.activity.SongsViewActivity", 3000);
//
//        View settings = solo.getView(R.id.action_settings);
//        solo.clickOnView(settings);
//
//        solo.waitForActivity("org.worshipsongs.activity.UserSettingActivity", 3000);
//
//        solo.clickOnText("Reset to default");
//
//        assertEquals("String Found", true, solo.searchText("Reset to default"));
//    }
//}