package org.worshipsongs.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.worshipsongs.fragment.HomeTabFragment;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.worship.R;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * author:Madasamy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer
{

    private static final String SENDER_MAIL = "technical@mcruncher.com";
    private PresentationScreenService presentationScreenService;

    @Override
    public void init(Bundle bundle)
    {
        presentationScreenService = new PresentationScreenService(this);
        this.addSubheader("");
        this.addSection(newSection(getString(R.string.home), R.drawable.ic_library_books_white, new HomeTabFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_white, getSettings()));
        this.addSection(newSection(getString(R.string.rate_this_app), android.R.drawable.star_off, getRateThisAppOnClickListener()));
        this.addSection(newSection(getString(R.string.share), android.R.drawable.ic_menu_share, getShare()));
        this.addSection(newSection(getString(R.string.feedback), android.R.drawable.sym_action_email, getEmail()));
        this.addBottomSection(newSection(getString(R.string.version) + " " + CommonUtils.getProjectVersion(), getVersionOnClickListener()));
    }

    private Intent getSettings()
    {
        return new Intent(NavigationDrawerActivity.this, UserSettingActivity.class);
    }

    private MaterialSectionListener getRateThisAppOnClickListener()
    {
        return new MaterialSectionListener()
        {
            @Override
            public void onClick(MaterialSection section)
            {
                Uri uri = Uri.parse("market://details?id=" + NavigationDrawerActivity.this.getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(getFlags());
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + NavigationDrawerActivity.this.getApplicationContext().getPackageName())));
                }
            }
        };
    }

    int getFlags()
    {
        return (Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    private Intent getShare()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_description) + getString(R.string.share_info));
        shareIntent.setType("text/plain");
        Intent intent = Intent.createChooser(shareIntent, getString(R.string.share) + " " + getString(R.string.app_name) + " in");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private Intent getEmail()
    {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setData(Uri.parse("mailto:" + SENDER_MAIL));
        mailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
        return Intent.createChooser(mailIntent, "");
    }

    private MaterialSectionListener getVersionOnClickListener()
    {
        return new MaterialSectionListener()
        {
            @Override
            public void onClick(MaterialSection section)
            {

            }
        };
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
