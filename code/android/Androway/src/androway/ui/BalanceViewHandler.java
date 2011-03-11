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
public class BalanceViewHandler extends LinearLayout
{
    private float _speed;
    private float _direction;
    private LinearLayout _moveArrow;
    private static final int _imgBgMargin = 5;

    public BalanceViewHandler(Context context)
    {
        super(context);        
        _moveArrow = new LinearLayout(context);
        _moveArrow.setBackgroundResource(R.drawable.arrow_move);
        this.addView(_moveArrow);
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed,l, t, r, b);
    }
}