package proj.androway.ui.block_component;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;

/**
 * The InclinationBlock is the class that shows the inclination of the Androway
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class InclinationBlock extends BlockComponent
{
    private float _inclDegrees = 0;
    private float _inclPreviousDegrees = 0;

    public InclinationBlock(Context context, SharedObjects sharedObjects, int layoutId)
    {
        super(context, sharedObjects, layoutId, BlockComponent.ID_BLOCK_1);
    }

    /**
     * Update the inclination block
     * @param updateType    The update type (UPDATE_TYPE_'update type')
     * @param params        The parameters (new data)
     */
    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
        if(updateType.equals(BlockComponent.UPDATE_TYPE_SESSION_DATA))
        {
            _inclDegrees = _sharedObjects.incomingData.inclination;
            setInclinationRotation(_inclDegrees, _inclPreviousDegrees, 200, new RotationListener());

            TextView inclinationLabel = (TextView)findViewById(R.id.inclination_degrees_label);
            inclinationLabel.setText(_inclDegrees + "°");
        }
    }

    /**
     * Set the new rotation for the inclination
     * @param degrees           The absolute degrees for the rotation
     * @param previousDegrees   The absolute degrees of the previous position
     * @param duration          The duration of the animation
     * @param rotationListener  The rotation listener
     */
    private void setInclinationRotation(float degrees, float previousDegrees, int duration, RotationListener rotationListener)
    {
        ImageView inclinationImage = (ImageView)findViewById(R.id.segway_body);

        if(degrees == previousDegrees)
            degrees += 0.001f;

        RotateAnimation rotateAnim = new RotateAnimation(previousDegrees, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
        rotateAnim.setDuration(duration);
        rotateAnim.setAnimationListener(rotationListener);

        inclinationImage.setAnimation(rotateAnim);

        // Store the current degrees for future use
        _inclPreviousDegrees = degrees;
    }

    private class RotationListener implements AnimationListener
    {
        public void onAnimationStart(Animation a) { }

        public void onAnimationEnd(Animation a)
        {
            setInclinationRotation(_inclDegrees, _inclPreviousDegrees, 150, this);
        }

        public void onAnimationRepeat(Animation a) { }
    }
}