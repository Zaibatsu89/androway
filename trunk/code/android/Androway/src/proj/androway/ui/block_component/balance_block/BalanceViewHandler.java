package proj.androway.ui.block_component.balance_block;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.main.TiltControls;

/**
 * The BalanceViewHandler class for drawing the phone balance
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class BalanceViewHandler extends LinearLayout
{
    private float _speed;
    private float _direction;
    private Display _display;
    private LinearLayout _moveArrow;
    private SharedObjects _sharedObjects;
    private static final int _imgBgMargin = 5;

    /**
     * The constructor for the BalanceViewHandler
     * @param context       The application context
     * @param sharedObjects An instance of the common SharedObjects object
     */
    public BalanceViewHandler(Context context, SharedObjects sharedObjects)
    {
        super(context);

        _sharedObjects = sharedObjects;

        // This background color is necessary, otherwise the element will not be visible
        this.setBackgroundColor(Color.parseColor("#00FF99FF"));

        _display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
        _moveArrow = new LinearLayout(context);
        _moveArrow.setBackgroundResource(R.drawable.arrow_move);
        this.addView(_moveArrow);
    }

    /**
     * Update the balance view
     * @param params    The parameters (new data)
     */
    public void updateView(Map params)
    {
        float azimuth = (Float)params.get(TiltControls.UPDATE_AZIMUTH);
        float roll = (Float)params.get(TiltControls.UPDATE_ROLL);
        float pitch = (Float)params.get(TiltControls.UPDATE_PITCH);
        float sensorA = 0f, sensorB = 0f, sensorC = 0f;

        switch (_display.getRotation())
        {
            case Surface.ROTATION_0:
            {
                sensorA = azimuth;
                sensorB = -roll;
                sensorC = pitch;
                break;
            }
            case Surface.ROTATION_90:
            {
                sensorA = -roll;
                sensorB = -azimuth;
                sensorC = pitch;
                break;
            }
            case Surface.ROTATION_180:
            {
                sensorA = -azimuth;
                sensorB = roll;
                sensorC = -pitch;
                break;
            }
            case Surface.ROTATION_270:
            {
                sensorA = roll;
                sensorB = azimuth;
                sensorC = pitch;
                break;
            }
        }

        if(Settings.DEVICE_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            // Convert the sensor values to the actual speed and direction values
            float direction = getAnswer(sensorA, sensorB, 0) * -9.8f;
            float speed = getAnswer(sensorA, sensorB, 1) * -10.4f;

            // Process the desired balance sensitivity
            if(direction != 0)
                direction *= Settings.BALANCE_CONTROL_SENSITIVITY;

            // Limit the maximum direction value
            if(direction > 100)
                direction = 100;
            else if(direction < -100)
                direction = -100;

            // Store the calculated direction
            _direction = direction;
            
            if (sensorC > 0 && direction != -100 && direction != 100)
            {
                // Handle the desired offset
                speed -= Settings.BALANCE_CONTROL_OFFSET;

                // If an offset is set, the angle used for accelleration is smaller. So multiply the speed.
                if(Settings.BALANCE_CONTROL_OFFSET > 0 && speed > 0 && speed < 100)
                    speed *= 100f / ((100f - (float)Settings.BALANCE_CONTROL_OFFSET));
                
                // Process the desired balance sensitivity
                if(speed != 0)
                    speed *= Settings.BALANCE_CONTROL_SENSITIVITY;

                // Limit the maximum speed value
                if(speed > 100)
                    speed = 100;
                else if(speed < -100)
                    speed = -100;

                // Store the calculated speed
                _speed = speed;
            }
        }
        else
        {
            float direction = getAnswer(sensorA, sensorC, 1) * 9.8f;
            float speed = getAnswer(sensorA, sensorC, 0) * -10.4f;

            // Process the desired balance sensitivity
            if(direction != 0)
                direction *= Settings.BALANCE_CONTROL_SENSITIVITY;

            // Limit the maximum direction value
            if(direction > 100)
                direction = 100;
            else if(direction < -100)
                direction = -100;

            // Store the calculated direction
            _direction = direction;

            if (sensorB < 0 && direction != -100 && direction != 100)
            {
                // Handle the desired offset
                speed -= Settings.BALANCE_CONTROL_OFFSET;

                // If an offset is set, the angle used for accelleration is smaller. So multiply the speed.
                if(Settings.BALANCE_CONTROL_OFFSET > 0 && speed > 0 && speed < 100)
                    speed *= 100f / ((100f - (float)Settings.BALANCE_CONTROL_OFFSET));

                // Process the desired balance sensitivity
                if(speed != 0)
                    speed *= Settings.BALANCE_CONTROL_SENSITIVITY;

                // Limit the maximum speed value
                if(speed > 100)
                    speed = 100;
                else if(speed < -100)
                    speed = -100;

                // Store the calculated speed
                _speed = speed;
            }
        }

        // Store the calculated direction and speed in the outgoingData object
        _sharedObjects.outgoingData.drivingDirection = _direction;
        _sharedObjects.outgoingData.drivingSpeed = _speed;

        this.invalidate();
    }

    /**
     * Calculates a position based on the given coördinates
     * @param value1    The first coördinate-axis value
     * @param value2    The second coördinate-axis value
     * @param which     Which type to use
     * @return The new calculated position
     */
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

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float rectMargin = 0;
        float lineMargin = 0;

        // Determine which margins to use based on the phone its orientation
        switch(Settings.DEVICE_ORIENTATION)
        {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                rectMargin = 10;
                lineMargin = 50;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                rectMargin = 15;
                lineMargin = 30;
                break;
        }

        /*
         * Calculate the size of the rectangle to draw based on the width and
         * height of the parent layout.
         *
         * The rectangle is a square so it will take the width or height of the
         * parent layout (whichever is smaller) and substract the rectangle margin
         * from it twice. The result will be used as as maximum size.
         */
        LinearLayout parentLayout = (LinearLayout)this.getParent();
        float rectSize = (parentLayout.getWidth() > parentLayout.getHeight() ? parentLayout.getHeight() - (2 * rectMargin) : parentLayout.getWidth() - (2 * rectMargin));
        float innerRectSize = rectSize - (2 * _imgBgMargin);

        // Setup the paint to use
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        paint.setStrokeWidth(3);

        // Draw the rectangle - uncomment the following lines in order to draw the rectangle for testing purposes
        /*
            paint.setColor(Color.RED);
            canvas.drawRect(rectSize + _imgBgMargin, _imgBgMargin, _imgBgMargin, rectSize + _imgBgMargin, paint);
        */

        // Draw a line in the center of the rectangle (width can be altered by changing the lineMargin)
        canvas.drawLine
        (
            innerRectSize - lineMargin,
            (innerRectSize / 2),
            lineMargin,
            (innerRectSize / 2),
            paint
        );

        // Convert the actual speed and direction values to the grid values
        int gridX = (int) (_direction * (((innerRectSize - 15) / 2) / 100) + (innerRectSize / 2)) - 9;
        int gridY = (int) (_speed * -(((innerRectSize - 15) / 2) / 100) + (innerRectSize / 2)) - 9;

        // Position the move arrow based on the given grid coordinates (gridX, gridY)
        LinearLayout.LayoutParams newLayoutParams = (LinearLayout.LayoutParams)_moveArrow.getLayoutParams();

        if(newLayoutParams == null)
            newLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        newLayoutParams.setMargins(gridX, gridY, 0, 0);
        _moveArrow.setLayoutParams(newLayoutParams);
        _moveArrow.requestLayout();
        
        // Set the new layout parameters for the inner linear element (the draw component).
        // This element is placed linear below the outer element so for correct
        // positioning a negative margin is set, to position it exactly on top of the outer element.
        newLayoutParams = (LinearLayout.LayoutParams)this.getLayoutParams();
        newLayoutParams.width = (int)innerRectSize + 3;
        newLayoutParams.height = (int)innerRectSize + 3;
        newLayoutParams.setMargins(3, -(int)(rectSize - _imgBgMargin), 0, 0);
        this.setLayoutParams(newLayoutParams);
        this.requestLayout();

        // Set the new layout parameters for the outer linear element (the background image component)
        LinearLayout balanceBg = (LinearLayout)parentLayout.findViewById(R.id.balance_holder);
        newLayoutParams = (LinearLayout.LayoutParams)balanceBg.getLayoutParams();
        newLayoutParams.width = (int)rectSize;
        newLayoutParams.height = (int)rectSize;
        newLayoutParams.setMargins(0, (int)rectMargin, 0, 0);        
        balanceBg.setLayoutParams(newLayoutParams);
        balanceBg.requestLayout();
    }
}