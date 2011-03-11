package androway.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import androway.ui.BalanceViewHandler;

/**
 * Class TiltControls connects with class Controller.
 * @author Tymen
 * @since 02-03-2011
 * @version 0.2
 */
public class TiltControls implements SensorEventListener
{
    private Context _context = null;
    private BalanceViewHandler _balanceView = null;
    private SensorManager _sensorManager = null;

    public TiltControls(Context context, BalanceViewHandler balanceView)
    {
        _context = context;
        _balanceView = balanceView;
        _sensorManager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
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

                // Convert the sensor values to the actual speed and direction values
                float speed = (pitch * 2.222f) + 200;
                float direction = roll * -2.222f;

                // Limit the maximum speed value
                if(speed > 100)
                    speed = 100;
                else if(speed < -100)
                    speed = -100;

                // Limit the maximum direction value
                if(direction > 100)
                    direction = 100;
                else if(direction < -100)
                    direction = -100;

                _balanceView.setBalance(speed, direction);
            }
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) { }
}