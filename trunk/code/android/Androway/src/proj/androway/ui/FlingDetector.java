package proj.androway.ui;

import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import proj.androway.common.Settings;
import proj.androway.ui.block_component.BlockComponent;
import proj.androway.R;

/**
 * The custom gesture class which is detects flings and triggers the paired flipper
 * @author Tymen
 * @since 18-02-2011
 * @version 0.4
 */
public class FlingDetector extends SimpleOnGestureListener
{
    private static final int _SWIPE_MIN_DISTANCE = 120;
    private static final int _SWIPE_MAX_OFF_PATH = 250;
    private static final int _SWIPE_THRESHOLD_VELOCITY = 200;

    private Animation _slideLeftIn;
    private Animation _slideLeftOut;
    private Animation _slideRightIn;
    private Animation _slideRightOut;

    private int _blockId;
    private ViewFlipper _flipper;
    private Vibrator _vibrator;

    public FlingDetector(Context context, int blockId, ViewFlipper flipper)
    {
        _blockId = blockId;
        _flipper = flipper;

        // Get an instance of Vibrator from given Context
        _vibrator =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

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
            if (Math.abs(e1.getY() - e2.getY()) > _SWIPE_MAX_OFF_PATH)
                return false;

            switch(_blockId)
            {
                case BlockComponent.ID_BLOCK_1:
                {
                    if(Settings.BLOCK_1_LOCKED)
                        return false;
                    break;
                }
                case BlockComponent.ID_BLOCK_2:
                {
                    if(Settings.BLOCK_2_LOCKED)
                        return false;
                    break;
                }
            }

            if(e1.getX() - e2.getX() > _SWIPE_MIN_DISTANCE && Math.abs(velocityX) > _SWIPE_THRESHOLD_VELOCITY)
            {
                // right to left swipe
                _flipper.setInAnimation(_slideLeftIn);
                _flipper.setOutAnimation(_slideLeftOut);
                _flipper.showNext();
                vibrateEffect();
            }
            else if (e2.getX() - e1.getX() > _SWIPE_MIN_DISTANCE && Math.abs(velocityX) > _SWIPE_THRESHOLD_VELOCITY)
            {
                // left2to right swipe
                _flipper.setInAnimation(_slideRightIn);
                _flipper.setOutAnimation(_slideRightOut);
                _flipper.showPrevious();
                vibrateEffect();
            }
        }
        catch (Exception e)
        {
            
        }

        return false;
    }

    public void vibrateEffect()
    {
        long[] pattern =
        {
            0,  // Start immediately
            50, // Vibrate a long time
            550,// Pause a short time
            50  // Vibrate a short time
        };

        // Only perform this pattern one time (-1 means "do not repeat")
        _vibrator.vibrate(pattern, -1);
    }
}