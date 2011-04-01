package org.me.android_experiment_04;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

/**
 *
 * @author Rinse
 */
public class DrawDemo extends Activity {
	private DemoView mDemoView;
	private SensorManager mSensorManager = null;
	private SensorEvent sensorEvent;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private long mSensorTimeStamp;
    private long mCpuTimeStamp;
	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get a reference to a SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

		mDemoView = new DemoView(this);
		setContentView(mDemoView);
	}

	@Override
    protected void onResume() {
		super.onResume();

		mDemoView.startSimulation();
    }

    @Override
    protected void onPause() {
		super.onPause();

		mDemoView.stopSimulation();
    }

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	private class DemoView extends View implements SensorEventListener {
		private Paint paint;
		private Canvas canvas;
		private Sensor mAccelerometer;
		private long mLastT;
        private float mLastDeltaT;

		public void startSimulation() {
            /*
             * It is not necessary to get accelerometer events at a very high
             * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
             * automatic low-pass filter, which "extracts" the gravity component
             * of the acceleration. As an added benefit, we use less power and
             * CPU resources.
             */
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

		public DemoView(Context context) {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}

		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
				return;
			/*
			 * record the accelerometer data, the event's timestamp as well as
			 * the current time. The latter is needed so we can calculate the
			 * "present" time during rendering. In this application, we need to
			 * take into account how the screen is rotated with respect to the
			 * sensors (which always return data in a coordinate space aligned
			 * to with the screen in its native orientation).
			 */

			switch (mDisplay.getRotation()) {
				case Surface.ROTATION_0:
					mSensorX = event.values[0];
					mSensorY = -event.values[2];
					mSensorZ = event.values[1];
					break;
				case Surface.ROTATION_90:
					mSensorX = -event.values[2];
					mSensorY = -event.values[0];
					mSensorZ = event.values[1];
					break;
				case Surface.ROTATION_180:
					mSensorX = -event.values[0];
					mSensorY = event.values[2];
					mSensorZ = -event.values[1];
					break;
				case Surface.ROTATION_270:
					mSensorX = event.values[2];
					mSensorY = event.values[0];
					mSensorZ = event.values[1];
					break;
			}

			mSensorTimeStamp = event.timestamp;
			mCpuTimeStamp = System.nanoTime();
		}

		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			// initiate paint
			paint = new Paint();
			this.canvas = canvas;
			paint.setAntiAlias(true);

			// make the entire canvas white
			paint.setColor(Color.WHITE);
			canvas.drawPaint(paint);

			// paint rectangle with line
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawRect(300, 132, 30, 402, paint);
			canvas.drawLine(105, 267, 225, 267, paint);

			canvas.drawText("Sensor X: " + String.valueOf(mSensorX), 10, 30, paint);
			canvas.drawText("Sensor Y: " + String.valueOf(mSensorY), 10, 40, paint);
			canvas.drawText("Sensor Z: " + String.valueOf(mSensorZ), 10, 50, paint);

			int tempX = (int)(getAnswer(mSensorX, mSensorY, 0) * -9.8f);
			int tempY = (int)(getAnswer(mSensorX, mSensorY, 1) * -10.4f);

			canvas.drawText("Grid X: " + String.valueOf(tempX), 10, 70, paint);
			canvas.drawText("Grid Y: " + String.valueOf(tempY), 10, 80, paint);

			if (tempX >= -100 && tempX <= 100 && tempY >= -100 && tempY <= 100 && mSensorZ >= 0f)
				setRichting(tempX, tempY);

			final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);
            final float sx = mSensorX;
            final float sy = mSensorY;

			updatePositions(sx, sy, now);

			// and make sure to redraw asap
            invalidate();
		}

		private float getAnswer(float value1, float value2, int which)
		{
			float diff = Math.abs(Math.abs(value1) - Math.abs(value2));
			float power = 1.45f - (0.045f * diff);
			float value = 0f;

			switch (which)
			{
				case 0:
					value = value1 * power;
					break;
				case 1:
					value = value2 * power;
					break;
			}

			return value;
		}

		/*
		 * Update the position of each particle in the system using the
		 * Verlet integrator.
		 */
		private void updatePositions(float sx, float sy, long timestamp) {
			final long t = timestamp;
			if (mLastT != 0) {
				final float dT = (float) (t - mLastT) * (1.0f / 1000000000.0f);
				if (mLastDeltaT != 0) {
					final float dTC = dT / mLastDeltaT;
				}
				mLastDeltaT = dT;
			}
			mLastT = t;
		}

		private void setRichting(int x, int y) {
			paint.setColor(Color.BLACK);

			// Draw the labels
			if (x < 0)
				canvas.drawText("L", 162, 125, paint);
			else if (x == 0)
				canvas.drawText("S", 162, 125, paint);
			else
				canvas.drawText("R", 162, 125, paint);

			if (y < 0)
				canvas.drawText("B", 10, 270, paint);
			else if (y == 0)
				canvas.drawText("P", 10, 270, paint);
			else
				canvas.drawText("F", 10, 270, paint);

			setStip(x, y);
		}

		private void setStip(int x, int y)
		{
			x = (int) (x * 1.35f + 135);
			y = (int) (y * -1.35f + 135);

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);
			canvas.drawCircle(x + 30, y + 132, 3, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawCircle(x + 30, y + 132, 3, paint);
		}

		
	}
}