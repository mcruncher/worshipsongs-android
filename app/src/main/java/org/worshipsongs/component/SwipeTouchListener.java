package org.worshipsongs.component;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * author: madasamy
 * version:2.1.0
 */
public class SwipeTouchListener implements View.OnTouchListener
{

    private Activity activity;
    // TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    static final int MIN_DISTANCE = 50;
    private float downX, downY, upX, upY;

    // private MainActivity mMainActivity;
    public SwipeTouchListener(Activity mainActivity)
    {
        activity = mainActivity;
    }

    public void onRightToLeftSwipe()
    {
        Log.i(this.getClass().getSimpleName(), "RightToLeftSwipe!");
        Toast.makeText(activity, "RightToLeftSwipe", Toast.LENGTH_SHORT).show();
        // activity.doSomething();
    }

    public void onLeftToRightSwipe()
    {
        Log.i(this.getClass().getSimpleName(), "LeftToRightSwipe!");
        Toast.makeText(activity, "LeftToRightSwipe", Toast.LENGTH_SHORT).show();
        // activity.doSomething();
    }

    public void onTopToBottomSwipe()
    {
        Log.i(this.getClass().getSimpleName(), "onTopToBottomSwipe!");
        // Toast.makeText(activity, "onTopToBottomSwipe", Toast.LENGTH_SHORT).show();
        // activity.doSomething();
    }

    public void onBottomToTopSwipe()
    {
        Log.i(this.getClass().getSimpleName(), "onBottomToTopSwipe!");
        // Toast.makeText(activity, "onBottomToTopSwipe", Toast.LENGTH_SHORT).show();
        // activity.doSomething();
    }

    public void onClick()
    {

    }

    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(this.getClass().getSimpleName(), "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                   // Log.i(this.getClass().getSimpleName(), "Swipe was only " + Math.abs(deltaX) + "  vertical long, need at least " + MIN_DISTANCE);
                    v.performClick();
                }
            }
        }
        return false;
    }

}


