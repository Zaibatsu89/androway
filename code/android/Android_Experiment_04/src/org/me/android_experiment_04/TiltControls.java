package org.me.android_experiment_04;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

/**
 *
 * @author Rinse
 */
public class TiltControls extends Activity implements SensorEventListener {
	// Orientation X, Y, and Z values
	private TextView orientXValue;
	private TextView orientYValue;
	private TextView orientZValue;

	private SensorEvent mSensorEvent = null;
	private SensorManagerSimulator mSensorManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

		// Get a reference to a SensorManager
        mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);

		setContentView(R.layout.main);

		mSensorManager.connectSimulator();

        // Capture orientation related view elements
        orientXValue = (TextView) findViewById(R.id.orient_x_value);
        orientYValue = (TextView) findViewById(R.id.orient_y_value);
        orientZValue = (TextView) findViewById(R.id.orient_z_value);

        // Initialize orientation related view elements
        orientXValue.setText("0.00");
        orientYValue.setText("0.00");
        orientZValue.setText("0.00");
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		synchronized (this) {
			orientXValue.setText(Float.toString(mSensorEvent.values[0]));
			orientYValue.setText(Float.toString(mSensorEvent.values[1]));
			orientZValue.setText(Float.toString(mSensorEvent.values[2]));
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
    protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManagerSimulator.SENSOR_DELAY_FASTEST);
	}

    @Override
    protected void onStop() {
		// Unregister the listener
		mSensorManager.unregisterListener(this);
		super.onStop();
    }
}