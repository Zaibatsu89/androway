package proj.androway.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import proj.androway.ui.RunningSessionView;

/**
 * Class TiltControls connects with class Controller.
 * @author Tymen en Rinse
 * @since 04-04-2011
 * @version 0.31
 */
public class TiltControls implements SensorEventListener
{
    public static final String UPDATE_AZIMUTH = "azimuth";
    public static final String UPDATE_PITCH = "pitch";
    public static final String UPDATE_ROLL = "roll";
    public static final String UPDATE_SENSOR_TYPE = "sensorType";

    private SensorManager _sensorManager = null;
    private RunningSessionView _runningSessionView = null;
    private int _sensorType;

    public TiltControls(Context context, RunningSessionView view, int sensorType)
    {
        _runningSessionView = view;
        _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        _sensorType = sensorType;
    }

    public void register()
    {
        _sensorManager.registerListener(this, _sensorManager.getDefaultSensor(_sensorType), SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister()
    {
        _sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {
        synchronized (this)
        {
            float azimuth = sensorEvent.values[0];  // azimuth  rotation around the z-axis (0 = north, 90 = east, 180 = south, 270 = west)
            float pitch = sensorEvent.values[1];    // pitch    rotation around the x-axis (-180 to 180)
            float roll = sensorEvent.values[2];     // roll     rotation around the y-axis (-90 to 90)

            _runningSessionView.updateTiltViews(azimuth, pitch, roll, sensorEvent.sensor.getType());
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) { }
}