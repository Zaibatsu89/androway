package proj.androway.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * The TiltControls class handles the reading of the G- or Giro-sensor
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class TiltControls implements SensorEventListener
{
    /**
     * The update key for the azimuth value
     */
    public static final String UPDATE_AZIMUTH = "azimuth";

    /**
     * The update key for the pitch value
     */
    public static final String UPDATE_PITCH = "pitch";

    /**
     * The update key for the roll value
     */
    public static final String UPDATE_ROLL = "roll";

    /**
     * The update key for the sensor types
     */
    public static final String UPDATE_SENSOR_TYPE = "sensorType";

    private SensorManager _sensorManager = null;
    private int _sensorType;
    private TiltDataChangedListener _tiltDataChangedListener;

    /**
     *
     * @param context       The application context
     * @param sensorType    The sensor type to read. The value can be either Sensor.TYPE_ACCELEROMETER or Sensor.TYPE_ORIENTATION.
     */
    public TiltControls(Context context, int sensorType)
    {
        _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        _sensorType = sensorType;
    }

    /**
     * Restiger the sensor listener
     */
    public void register()
    {
        _sensorManager.registerListener(this, _sensorManager.getDefaultSensor(_sensorType), SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Unregister the sensor listener
     */
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

            // Trigger the update tilt listener
            if(_tiltDataChangedListener != null)
                _tiltDataChangedListener.updateTilt(azimuth, pitch, roll, sensorEvent.sensor.getType());
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    /**
     * Register the given tilt sensor changed listener (callback)
     * @param tiltDataChangedListener The tilt sensor changed listener (callback)
     */
    public void onTiltSensorChanged(TiltDataChangedListener tiltDataChangedListener)
    {
        _tiltDataChangedListener = tiltDataChangedListener;
    }

    /**
     * The tilt data changed listener. Triggered when there is new tilt sensor data.
     */
    public interface TiltDataChangedListener
    {
        /**
         * Handle the updated tilt data
         * @param azimuth       The new azimuth value
         * @param pitch         The new pitch value
         * @param roll          The new roll value
         * @param sensorType    The sensor type the udpate is for. The value can be either Sensor.TYPE_ACCELEROMETER or Sensor.TYPE_ORIENTATION.
         */
        public abstract void updateTilt (float azimuth, float pitch, float roll, int sensorType);
    }
}