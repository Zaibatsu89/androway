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
import proj.androway.main.TiltControls;

/**
 * The compass block
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public class CompassBlock extends BlockComponent
{
    private float _compDegrees = 0;
    private float _compPreviousDegrees = 0;

    public CompassBlock(Context context, int layoutId)
    {
        super(context, layoutId, BlockComponent.ID_BLOCK_1);
    }

    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
        int sensorType = (Integer)params.get(TiltControls.UPDATE_SENSOR_TYPE);
        if(updateType.equals(BlockComponent.UPDATE_TYPE_TILT) && sensorType == Sensor.TYPE_ORIENTATION)
        {
            // Store the compas degrees value
            _compDegrees = 360 - (Float)params.get(TiltControls.UPDATE_AZIMUTH);

            if(Settings.DEVICE_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                _compDegrees -= 90;

            setCompassRotation(_compDegrees, _compPreviousDegrees, 200, new RotationListener());
        }
    }

    private void setCompassRotation(float degrees, float previousDegrees, int duration, RotationListener rotationListener)
    {
        ImageView compassImage = (ImageView)findViewById(R.id.compass);

        if(degrees == previousDegrees)
            degrees += 0.001f;

        RotateAnimation rotateAnim = new RotateAnimation(previousDegrees, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(duration);
        rotateAnim.setAnimationListener(new RotationListener());

        compassImage.setAnimation(rotateAnim);

        // Store the current degrees for future use
        _compPreviousDegrees = degrees;
    }

    public class RotationListener implements AnimationListener
    {
        public void onAnimationStart(Animation a) { }

        public void onAnimationEnd(Animation a)
        {
            setCompassRotation(_compDegrees, _compPreviousDegrees, 200, this);
        }

        public void onAnimationRepeat(Animation a) { }
    }
}
