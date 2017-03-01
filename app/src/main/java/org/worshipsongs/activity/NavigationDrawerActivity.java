package org.worshipsongs.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.service.SongListAdapterService;
import org.worshipsongs.worship.R;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer
{

    private PresentationScreenService presentationScreenService;

    @Override
    public void init(Bundle bundle)
    {
        presentationScreenService = new PresentationScreenService(this);
        this.addSubheader("");
        this.addSection(newSection(getString(R.string.home), R.drawable.ic_library_books_white, new HomeTabFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_white, getSettings()));
        this.addSection(newSection(getString(R.string.rate_this_app), android.R.drawable.star_off, getPlayStore()));
        this.addSection(newSection(getString(R.string.share), android.R.drawable.ic_menu_share, getShare()));

    }

    private Intent getSettings()
    {
        return new Intent(NavigationDrawerActivity.this, UserSettingActivity.class);
    }

    private Intent getPlayStore()
    {
        Uri uri = Uri.parse("market://details?id=" + this.getApplicationContext().getPackageName());
        Intent playStore = new Intent(Intent.ACTION_VIEW, uri);
        playStore.addFlags(getFlags());
        return playStore;
    }

    int getFlags()
    {
        return (Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    private Intent getShare()
    {
        Intent shareIntent = new Intent(Intent.CATEGORY_APP_EMAIL);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_info));
        shareIntent.setType("text/plain");
        Intent intent = Intent.createChooser(shareIntent, getString(R.string.share) + " " + getString(R.string.app_name) + " in");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;

    }

    @Override
    public void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        presentationScreenService.onResume();
    }
}
