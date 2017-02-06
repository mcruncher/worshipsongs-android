package org.worshipsongs.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRouter;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import org.worshipsongs.activity.NavigationDrawerActivity;
import org.worshipsongs.dialog.DefaultRemotePresentation;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class DefaultPresentationScreenService
{
    private Context context;
    private DefaultRemotePresentation defaultRemotePresentation;
    private DefaultPresentationScreenService.DefaultMediaRouterCallBack songMediaRouterCallBack = new DefaultPresentationScreenService.DefaultMediaRouterCallBack();
    private MediaRouter mediaRouter;

    public DefaultPresentationScreenService(Context context)
    {
        this.context = context;
        mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
    }


    //@Override
    public void onResume()
    {
        //super.onResume();
        if (isJellyBean()) {
            mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, songMediaRouterCallBack);
            updatePresentation();
        }
    }

    //@Override
    public void onPause()
    {

        if (isJellyBean()) {
            mediaRouter.removeCallback(songMediaRouterCallBack);
        }
    }

    //@Override
    public void onStop()
    {
        // BEGIN_INCLUDE(onStop)
        // Dismiss the presentation when the activity is not visible.
        if (defaultRemotePresentation != null) {
            defaultRemotePresentation.dismiss();
            defaultRemotePresentation = null;
        }

        // BEGIN_INCLUDE(onStop)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    class DefaultMediaRouterCallBack extends MediaRouter.SimpleCallback
    {

        @Override
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info)
        {
            updatePresentation();

        }

        @Override
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info)
        {
            updatePresentation();

        }

        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info)
        {
            updatePresentation();
        }

    }

    private final DialogInterface.OnDismissListener remoteDisplayDismissListener =
            new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    if (dialog == defaultRemotePresentation) {
                        defaultRemotePresentation = null;
                    }

                }
            };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updatePresentation()
    {
        Display selectedDisplay = getSelectedDisplay();
        if (defaultRemotePresentation != null && defaultRemotePresentation.getDisplay() != selectedDisplay) {
            defaultRemotePresentation.dismiss();
            defaultRemotePresentation = null;
        }

        if (defaultRemotePresentation == null && selectedDisplay != null) {
            // Initialise a new Presentation for the Display
            defaultRemotePresentation = new DefaultRemotePresentation(context, selectedDisplay);
            defaultRemotePresentation.setOnDismissListener(remoteDisplayDismissListener);
            try {
                defaultRemotePresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                defaultRemotePresentation = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Display getSelectedDisplay()
    {
        MediaRouter.RouteInfo selectedRoute = mediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display selectedDisplay = null;
        if (selectedRoute != null) {
            selectedDisplay = selectedRoute.getPresentationDisplay();
        }
        return selectedDisplay;
    }


    private boolean isJellyBean()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
