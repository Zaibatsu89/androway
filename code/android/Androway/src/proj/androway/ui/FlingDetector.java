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
 * The FlingDetector class is a custom gesture class which is detects flings and triggers the paired ViewFlipper
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class FlingDetector extends SimpleOnGestureListener
{
    /**
     * The minimal swipe distance for recognition
     */
    private static final int _SWIPE_MIN_DISTANCE = 120;

    /**
     * The maximum swipe offset of the path for recognition
     */
    private static final int _SWIPE_MAX_OFF_PATH = 250;

    /**
     * The minimum swipe velocity for recognition
     */
    private static final int _SWIPE_THRESHOLD_VELOCITY = 200;

    // The used animations
    private Animation _slideLeftIn;
    private Animation _slideLeftOut;
    private Animation _slideRightIn;
    private Animation _slideRightOut;

    private int _blockId;
    private ViewFlipper _flipper;
    private Vibrator _vibrator;

    /**
     * The constructor for the FlingDetector. Bind the given ViewFlipper to this FlingDetector.
     * @param context   The application context
     * @param blockId   The id of the flipper block (BlockComponent.ID_BLOCK_ #)
     * @param flipper   The ViewFlipper to bind to the FlingDetector
     */
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

            // Check if this block is locked. If so, return false (break), otherwise proceed.
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

    /**
     * Perform a vibrateEffect that corresponds with the sliding effect of the ViewFlipper
     */
    public void vibrateEffect()
    {
        long[] pattern =
        {
            0,  // Start immediately
            50, // Vibrate a short time
            550,// Pause a long time
            50  // Vibrate a short time
        };

        // Only perform this pattern one time (-1 means "do not repeat")
        _vibrator.vibrate(pattern, -1);
    }
}