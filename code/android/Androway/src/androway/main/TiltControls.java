package androway.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androway.ui.View;

/**
 * Class TiltControls connects with class Controller.
 * @author Tymen
 * @since 16-03-2011
 * @version 0.3
 */
public class TiltControls implements SensorEventListener
{
    private SensorManager _sensorManager = null;
    private View _view = null;

    public TiltControls(Context context, View view)
    {
        _view = view;
        _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void register()
    {
        _sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister()
    {
        _sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {        
        synchronized (this)
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
            {
                float azimuth = sensorEvent.values[0];  // azimuth  rotation around the z-axis (0 = north, 90 = east, 180 = south, 270 = west)
                float pitch = sensorEvent.values[1];    // pitch    rotation around the x-axis (-180 to 180)
                float roll = sensorEvent.values[2];     // roll     rotation around the y-axis (-90 to 90)

                _view.updateTiltViews(azimuth, pitch, roll);
            }
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) { }
}