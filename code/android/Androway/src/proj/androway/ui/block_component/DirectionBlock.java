package proj.androway.ui.block_component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;

/**
 * The DirectionBlock is the class that shows the direction block of the Androway (left, right, straight etc.)
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class DirectionBlock extends BlockComponent
{
    /**
     * The offset for when we show the 'going-straight' arrow
     */
    private static final int CENTER_SPEED_OFFSET = 5;
    
    private TextView _leftWheelLabel;
    private TextView _rightWheelLabel;
    private ImageView _directionArrowImage;

    public DirectionBlock(Context context, SharedObjects sharedObjects, int layoutId)
    {
        super(context, sharedObjects, layoutId, BlockComponent.ID_BLOCK_2);

        // Get and store the left and right wheel TextView object
        _leftWheelLabel = (TextView)findViewById(R.id.left_wheel_speed);
        _rightWheelLabel = (TextView)findViewById(R.id.right_wheel_speed);
        _directionArrowImage = (ImageView)findViewById(R.id.direction_arrow);
    }

    /**
     * Update the direction block
     * @param updateType    The update type (UPDATE_TYPE_'update type')
     * @param params        The parameters (new data)
     */
    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
        if(updateType.equals(BlockComponent.UPDATE_TYPE_SESSION_DATA))
        {
            int leftWheelSpeed = _sharedObjects.incomingData.leftWheelSpeed;
            int rightWheelSpeed = _sharedObjects.incomingData.rightWheelSpeed;
            int arrowImageId = -1;

            // Set the left and right wheel speed to the labels
            _leftWheelLabel.setText(String.valueOf(leftWheelSpeed) + "%");
            _rightWheelLabel.setText(String.valueOf(rightWheelSpeed) + "%");

            // Decide what image resource to use
            // (360 clockwise || 360 counterclockwise || left || right || straight)
            if(leftWheelSpeed < 0 && Math.abs(leftWheelSpeed) == rightWheelSpeed)
                arrowImageId = R.drawable.dir_arrow_360_left; // A 360 to the left (counterclockwise)
            else if(rightWheelSpeed < 0 && Math.abs(rightWheelSpeed) == leftWheelSpeed)
                arrowImageId = R.drawable.dir_arrow_360_right; // A 360 to the right (clockwise)
            else if(leftWheelSpeed < rightWheelSpeed - CENTER_SPEED_OFFSET)
                 arrowImageId = R.drawable.dir_arrow_left; // Going left
            else if(rightWheelSpeed < leftWheelSpeed - CENTER_SPEED_OFFSET)
                arrowImageId = R.drawable.dir_arrow_right; // Going right
            else
                arrowImageId = R.drawable.dir_arrow_straight; // Going straight

            if(leftWheelSpeed < 0 && rightWheelSpeed < 0)
            {
                // Going backward, so we need to flip the images vertically and horizontally
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), arrowImageId);

                Matrix matrixFlip = new Matrix();
                matrixFlip.setTranslate(10, 10 + ( 2 * bmp.getHeight()) + bmp.getHeight());
		matrixFlip.preScale(-1.0f, -1.0f);
                
                Bitmap flippedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixFlip, true);

                _directionArrowImage.setImageBitmap(flippedBmp);
            }
            else
            {
                // Going forward, no extra changed needed
                // Set the image resource id as the new arrow image
                _directionArrowImage.setImageResource(arrowImageId);
            }
        }
    }
}
