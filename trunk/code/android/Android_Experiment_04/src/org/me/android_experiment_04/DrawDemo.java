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

public class DrawDemo extends Activity
{
    private DemoView mDemoView;
    private SensorManager mSensorManager = null;
    private Display mDisplay;
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mDemoView = new DemoView(this);
        setContentView(mDemoView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mDemoView.startSimulation();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mDemoView.stopSimulation();
    }
    
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    
    private class DemoView extends View implements SensorEventListener
    {
        private Paint paint;
        private Canvas canvas;
        private Sensor mAccelerometer;

        public DemoView(Context context)
        {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        public void startSimulation()
        {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        public void stopSimulation()
        {
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event)
        {
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

            switch (mDisplay.getRotation())
            {
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
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) { }


        @Override
        protected void onDraw(Canvas canvas)
        {
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
                    setStip(tempX, tempY);

            // Redraw
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