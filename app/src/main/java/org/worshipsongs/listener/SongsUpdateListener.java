package org.worshipsongs.listener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.AlertDialogFragment;
import org.worshipsongs.task.HttpAsyncTask;
import org.worshipsongs.utils.CommonUtils;

import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Author : Madasamy
 * Version : 3.x
 */

//public class SongsUpdateListener implements MaterialSectionListener
//{
//    private Context context;
//
//    public SongsUpdateListener(Context context)
//    {
//        this.context = context;
//    }
//
//    @Override
//    public void onClick(MaterialSection section)
//    {
//        if (CommonUtils.isWifiOrMobileDataConnectionExists(context)) {
//            new HttpAsyncTask(context).execute("https://api.github.com/repos/mcruncher/worshipsongs-db-dev/git/refs/heads/master");
//        } else {
//            Bundle bundle = new Bundle();
//            bundle.putString(CommonConstants.TITLE_KEY, "Warning");
//            bundle.putString(CommonConstants.MESSAGE_KEY, "Internet connection is needed to update the song database");
//            AlertDialogFragment.newInstance(bundle).show(((Activity) context).getFragmentManager(), AlertDialogFragment.class.getSimpleName());
//        }
//    }
//}
