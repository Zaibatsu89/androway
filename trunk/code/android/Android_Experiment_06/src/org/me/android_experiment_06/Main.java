package org.me.android_experiment_06;

import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class Main extends Activity
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;

    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Assign the anmiations to the animation objects
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        // Initialize the gesture detectors
        _gestureDetectorBlock1 = new GestureDetector(new MyGestureDetector((ViewFlipper)findViewById(R.id.block1_flipper)));
        _gestureDetectorBlock2 = new GestureDetector(new MyGestureDetector((ViewFlipper)findViewById(R.id.block2_flipper)));

        // Bind the gesture detectors to the on touch events of the gesture overlays
        ((GestureOverlayView)findViewById(R.id.block1)).setOnTouchListener(new OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (_gestureDetectorBlock1.onTouchEvent(event))
                    return true;
                else
                    return false;
            }
        });

        ((GestureOverlayView)findViewById(R.id.block2)).setOnTouchListener(new OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (_gestureDetectorBlock2.onTouchEvent(event))
                    return true;
                else
                    return false;
            }
        });
    }

    class MyGestureDetector extends SimpleOnGestureListener
    {
        private ViewFlipper _flipper;

        public MyGestureDetector(ViewFlipper flipper)
        {
            _flipper = flipper;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            try
            {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;

                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                    // right to left swipe
                    _flipper.setInAnimation(slideLeftIn);
                    _flipper.setOutAnimation(slideLeftOut);
                    _flipper.showNext();
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                    // left2to right swipe
                    _flipper.setInAnimation(slideRightIn);
                    _flipper.setOutAnimation(slideRightOut);
                    _flipper.showPrevious();
                }
            }
            catch (Exception e)
            {
                // nothing
            }

            return false;
        }
    }
}