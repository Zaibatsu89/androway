package androway.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;

/**
 * Class View for drawing the balance in the UI.
 * @author Tymen
 * @since 02-03-2011
 * @version 0.2
 */
public class BalanceView extends android.view.ViewGroup
{
    private float _speed;
    private float _direction;
    private static final int _imgBgMargin = 5;

    public BalanceView(Context context)
    {
        super(context);
    }

    public void setBalance(float speed, float direction)
    {
        _speed = speed;
        _direction = direction;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float rectMargin = 0;
        float lineMargin = 0;

        // Determine which margins to use based on the phone its orientation
        switch(getResources().getConfiguration().orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                rectMargin = 10;
                lineMargin = 50;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                rectMargin = 15;
                lineMargin = 30;
                break;
        }

        /**
         * Calculate the size of the rectangle to draw based on the width and
         * height of the parent layout.
         *
         * The rectangle is a square so it will take the width or height of the
         * parent layout (whichever is smaller) and substract the rectangle margin
         * from it twice. The result will be used as as maximum size.
         */
        LinearLayout parentLayout = (LinearLayout)this.getParent();
        float rectSize = (parentLayout.getWidth() > parentLayout.getHeight() ? parentLayout.getHeight() - (2 * rectMargin) : parentLayout.getWidth() - (2 * rectMargin));

        // Setup the paint to use
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        // Draw the rectangle - uncomment the following lines in order to draw the rectangle for testing purposes
        /*
            paint.setColor(Color.RED);
            canvas.drawRect(rectSize + _imgBgMargin, _imgBgMargin, _imgBgMargin, rectSize + _imgBgMargin, paint);
        */

        // Draw a line in the center of the rectangle (width can be altered by changing the lineMargin)
        canvas.drawLine
        (
            rectSize - lineMargin + _imgBgMargin,
            (rectSize / 2) + _imgBgMargin,
            lineMargin + _imgBgMargin,
            (rectSize / 2) + _imgBgMargin,
            paint
        );

        // Convert the actual speed and direction values to the grid values
        int gridX = (int) (_direction * ((rectSize / 2) / 100) + (rectSize / 2)) + _imgBgMargin;
        int gridY = (int) (_speed * (((rectSize / 2) / 100) * -1) + (rectSize / 2)) + _imgBgMargin;

        // Draw the bullet based on the given grid coordinates (gridX, gridY) and the size of the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(gridX, gridY, 3, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(gridX, gridY, 3, paint);

        // Set the new layout parameters for the BalanceView layout (rectangle holder)
        LinearLayout.LayoutParams newLayoutParams = (LinearLayout.LayoutParams)this.getLayoutParams();
        newLayoutParams.width = (int)rectSize + (_imgBgMargin * 2);
        newLayoutParams.height = (int)rectSize + (_imgBgMargin * 2);
        newLayoutParams.setMargins(0, (int)rectMargin, 0, 0);
        this.setLayoutParams(newLayoutParams);
        this.requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        for(int i = 0 ; i < this.getChildCount() ; i++)
            this.getChildAt(i).layout(l, t, r, b);
    }
}