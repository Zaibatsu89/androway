package proj.androway.ui.block_component;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import proj.androway.R;
import java.util.Map;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.main.TiltControls;

/**
 * The CompassBlock is the class for the compass view
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class CompassBlock extends BlockComponent
{
    private float _compDegrees = 0;
    private float _compPreviousDegrees = 0;

    public CompassBlock(Context context, SharedObjects sharedObjects, int layoutId)
    {
        super(context, sharedObjects, layoutId, BlockComponent.ID_BLOCK_1);
    }

    /**
     * Update the compass view
     * @param updateType    The update type (UPDATE_TYPE_'update type')
     * @param params        The parameters (new data)
     */
    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
        if(updateType.equals(BlockComponent.UPDATE_TYPE_TILT))
        {
            int sensorType = (Integer)params.get(TiltControls.UPDATE_SENSOR_TYPE);

            if(sensorType == Sensor.TYPE_ORIENTATION)
            {
                // Store the compas degrees value
                _compDegrees = 360 - (Float)params.get(TiltControls.UPDATE_AZIMUTH);

                if(Settings.DEVICE_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    _compDegrees -= 90;

                setCompassRotation(_compDegrees, _compPreviousDegrees, 200, new RotationListener());
            }
        }
    }

    /**
     * Set the new rotation for the compass
     * @param degrees           The absolute degrees for the rotation
     * @param previousDegrees   The absolute degrees of the previous position
     * @param duration          The duration of the animation
     * @param rotationListener  The rotation listener
     */
    private void setCompassRotation(float degrees, float previousDegrees, int duration, RotationListener rotationListener)
    {
        ImageView compassImage = (ImageView)findViewById(R.id.compass);

        if(degrees == previousDegrees)
            degrees += 0.001f;

        RotateAnimation rotateAnim = new RotateAnimation(previousDegrees, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(duration);
        rotateAnim.setAnimationListener(rotationListener);

        compassImage.setAnimation(rotateAnim);

        // Store the current degrees for future use
        _compPreviousDegrees = degrees;
    }

    private class RotationListener implements AnimationListener
    {
        public void onAnimationStart(Animation a) { }

        public void onAnimationEnd(Animation a)
        {
            setCompassRotation(_compDegrees, _compPreviousDegrees, 200, this);
        }

        public void onAnimationRepeat(Animation a) { }
    }
}
