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
import android.view.View;

/**
 *
 * @author Rinse
 */
public class DrawDemo extends Activity implements SensorEventListener {
	DemoView demoview;
	SensorManager sensorManager = null;
	SensorEvent sensorEvent;
	float sensorX;
	float sensorY;
	float sensorZ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				sensorX = sensorEvent.values[0];
				sensorY = sensorEvent.values[1];
				sensorZ = sensorEvent.values[2];
				demoview = new DemoView(this);
				setContentView(demoview);
			}
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
    protected void onResume() {
    super.onResume();
    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
    // Unregister the listener
    sensorManager.unregisterListener(this);
    super.onStop();
    }

	private class DemoView extends View {
		Paint paint;
		Canvas canvas;

		public DemoView(Context context){
			super(context);
		}

		@Override protected void onDraw(Canvas canvas) {
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

			canvas.drawText("Sensor X: " + String.valueOf(sensorX), 10, 30, paint);
			canvas.drawText("Sensor Y: " + String.valueOf(sensorY), 10, 40, paint);
			canvas.drawText("Sensor Z: " + String.valueOf(sensorZ), 10, 50, paint);

			int tempX = (int)(sensorZ * -1.111f);
			int tempY = (int)(sensorY * 1.111f) + 100;

			canvas.drawText("Grid X: " + String.valueOf(tempX), 10, 70, paint);
			canvas.drawText("Grid Y: " + String.valueOf(tempY), 10, 80, paint);

			if (tempX > -100 && tempX < 100 && tempY > -100 && tempY < 100)
				setRichting((int)(sensorZ * -1.111f), (int)(sensorY * 1.111f) + 100);
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