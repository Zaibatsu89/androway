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
 * The inclination block
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public class DirectionBlock extends BlockComponent
{
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

            // Decide what image resource to use (left || right || straight)
            if(leftWheelSpeed < rightWheelSpeed - CENTER_SPEED_OFFSET)
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
