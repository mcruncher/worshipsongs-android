package org.worshipsongs.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRouter;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;

import org.worshipsongs.dialog.RemoteSongPresentation;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.utils.CommonUtils;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class PresentationScreenService
{
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private Context context;
    private RemoteSongPresentation remoteSongPresentation;
    private PresentationScreenService.DefaultMediaRouterCallBack songMediaRouterCallBack = new PresentationScreenService.DefaultMediaRouterCallBack();
    private MediaRouter mediaRouter;

    public PresentationScreenService()
    {

    }

    public PresentationScreenService(Context context)
    {
        this.context = context;
        mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onResume()
    {
        if (isJellyBean() && CommonUtils.isProductionMode()) {
            mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, songMediaRouterCallBack);
            updatePresentation();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onPause()
    {
        if (isJellyBean() && CommonUtils.isProductionMode()) {
            mediaRouter.removeCallback(songMediaRouterCallBack);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onStop()
    {
        try {
            if (remoteSongPresentation != null) {
                remoteSongPresentation.cancel();
                remoteSongPresentation = null;
            }
        } catch (Exception ex) {
            Log.e(PresentationScreenService.class.getSimpleName(), "Error occurred while dismiss remote display");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private class DefaultMediaRouterCallBack extends MediaRouter.SimpleCallback
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
                    if (dialog == remoteSongPresentation) {
                        remoteSongPresentation = null;
                    }

                }
            };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updatePresentation()
    {
        try {
            Display selectedDisplay = getSelectedDisplay();
            if (remoteSongPresentation != null && remoteSongPresentation.getDisplay() != selectedDisplay) {
                if (remoteSongPresentation.isShowing()) {
                    remoteSongPresentation.dismiss();
                }
                remoteSongPresentation = null;
            }

            if (remoteSongPresentation == null && selectedDisplay != null) {
                remoteSongPresentation = new RemoteSongPresentation(context, selectedDisplay);
                remoteSongPresentation.setOnDismissListener(remoteDisplayDismissListener);
                remoteSongPresentation.show();
            }
            if (remoteSongPresentation != null && selectedDisplay != null) {
                if (Setting.getInstance().getSong() != null) {
                    showNextVerse(Setting.getInstance().getSong(), Setting.getInstance().getSlidePosition());
                }
            }
        } catch (Exception ex) {
            remoteSongPresentation = null;
            Log.e(PresentationScreenService.class.getSimpleName(), "Error occurred while presenting remote display" + ex);
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

    public void showNextVerse(Song song, int position)
    {
        try {
            if (remoteSongPresentation != null) {
                remoteSongPresentation.setVerseVisibility(View.VISIBLE);
                remoteSongPresentation.setImageViewVisibility(View.GONE);
                remoteSongPresentation.setVerse(song.getContents().get(position));
                remoteSongPresentation.setSongTitleAndChord(song.getTitle(), song.getChord(), preferenceSettingService.getPresentationPrimaryColor());
                remoteSongPresentation.setAuthorName(song.getAuthorName(), preferenceSettingService.getPresentationPrimaryColor());
                remoteSongPresentation.setSlidePosition(position, song.getContents().size(), preferenceSettingService.getPresentationPrimaryColor());
                Setting.getInstance().setSong(song);
                Setting.getInstance().setSlidePosition(position);
            }
        } catch (Exception e) {
            Log.e(PresentationScreenService.class.getSimpleName(), "Error occurred while presenting song content" + e);
        }
    }

    public RemoteSongPresentation getPresentation()
    {
        return remoteSongPresentation;
    }

}
