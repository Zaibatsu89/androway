package androway.ui;

import android.content.Context;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * The custom gesture class which is detects flings and triggers the paired flipper
 * @author Tymen
 * @since 18-02-2011
 * @version 0.4
 */
public class FlingDetector extends SimpleOnGestureListener
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Animation _slideLeftIn;
    private Animation _slideLeftOut;
    private Animation _slideRightIn;
    private Animation _slideRightOut;

    private ViewFlipper _flipper;

    public FlingDetector(Context context, ViewFlipper flipper)
    {
        _flipper = flipper;

        // Assign the anmiations to the animation objects
        _slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        _slideLeftOut = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        _slideRightIn = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        _slideRightOut = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
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
                _flipper.setInAnimation(_slideLeftIn);
                _flipper.setOutAnimation(_slideLeftOut);
                _flipper.showNext();
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // left2to right swipe
                _flipper.setInAnimation(_slideRightIn);
                _flipper.setOutAnimation(_slideRightOut);
                _flipper.showPrevious();
            }
        }
        catch (Exception e)
        {
            
        }

        return false;
    }
}