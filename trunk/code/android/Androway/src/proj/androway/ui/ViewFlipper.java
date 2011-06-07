package proj.androway.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * The ViewFlipper class doesn't do much but it is vital. The standard android ViewFlipper
 * crashes when switching the orientation of the application (manually or automatically).
 * When extending the default ViewFlipper and using this in the layout, the orientation-switch
 * bug is gone. Unclear why, because this class hardly does anything, but it works!
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class ViewFlipper extends android.widget.ViewFlipper
{
    public ViewFlipper(Context context)
    {
        super(context);
    }

    public ViewFlipper(Context context, AttributeSet attrs)
    {
    	super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow()
    {
    	try
        {
            super.onDetachedFromWindow();
    	}
        catch (IllegalArgumentException e)
        {
            // Call stopFlipping() in order to kick off updateRunning()
            this.stopFlipping();
        }
    }
}