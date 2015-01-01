package org.worshipsongs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.worshipsongs.worship.R;

/**
 * Created by Pitchu on 12/30/2014.
 */
public class ServiceListActivity extends Fragment
{
    private LinearLayout llLayout;
    private FragmentActivity faActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity  = (FragmentActivity)    super.getActivity();
        llLayout    = (LinearLayout)    inflater.inflate(R.layout.service_list_activity, container, false);

        return llLayout;
    }
}