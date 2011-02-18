package androway.ui;

import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import androway.logging.LocalManager;
import androway.logging.LoggingManager;
import androway.ui.quick_action.ActionItem;
import androway.ui.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.List;

/**
 * The main view of the system
 * @author Tymen en Rinse
 * @since 18-02-2011
 * @version 0.4
 */
public class View extends Activity
{
    private LoggingManager _lm;

    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        _lm = new LocalManager(this);

        final List<ActionItem> actionItems = new ArrayList<ActionItem>();

        /* EXAMPLE QUICK ACTION ITEMS WITH TEMP LOGGING AS ACTION */
            ActionItem connect = new ActionItem();
            //connect.setTitle(this.getString(R.string.connect));
            connect.setTitle(this.getString(R.string.add));
            connect.setIcon(getResources().getDrawable(R.drawable.bt_connect_icon));
            connect.setOnClickListener(new OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    //Toast.makeText(View.this, "Connecting to the Segway", Toast.LENGTH_SHORT).show();
                    _lm.add("NHL Hogeschool", "Minor Androway");
                }
            });
            actionItems.add(connect);

            ActionItem disconnect = new ActionItem();
            //disconnect.setTitle(this.getString(R.string.disconnect));
            disconnect.setTitle(this.getString(R.string.get));
            disconnect.setIcon(getResources().getDrawable(R.drawable.bt_disconnect_icon));
            disconnect.setOnClickListener(new OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    //Toast.makeText(View.this, "Disconnecting from the Segway", Toast.LENGTH_SHORT).show();
                    _lm.get();
                }
            });
            actionItems.add(disconnect);

            ActionItem settings = new ActionItem();
            //settings.setTitle(this.getString(R.string.settings));
            settings.setTitle(this.getString(R.string.remove));
            settings.setIcon(getResources().getDrawable(R.drawable.settings_icon));
            settings.setOnClickListener(new OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    //Toast.makeText(View.this, "Go to Bluetooth settings", Toast.LENGTH_SHORT).show();
                    _lm.remove();
                }
            });
            actionItems.add(settings);
        /* ------------------------------------------------------ */

        /* STATUSBAR BUTTON ONCLICK LISTENERS */
            ImageButton btButton = (ImageButton) this.findViewById(R.id.bluetooth_button);
            btButton.setOnClickListener(new android.view.View.OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    QuickAction quickAction = new QuickAction(v);
                    quickAction.setAnimStyle(QuickAction.ANIM_AUTO);

                    // Bind the action items to the QuickAction
                    for(ActionItem actionItem : actionItems)
                        quickAction.addActionItem(actionItem);

                    quickAction.show();
                }
            });

            ImageButton logWebButton = (ImageButton) this.findViewById(R.id.log_web_button);
            logWebButton.setOnClickListener(new android.view.View.OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.segway_body_inclination);

                    // Getting width & height of the given image.
                    int w = bmp.getWidth();
                    int h = bmp.getHeight();

                    // Setting post rotate to 90
                    Matrix mtx = new Matrix();
                    mtx.postRotate(45);

                    // Rotating Bitmap
                    Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
                    BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);

                    ImageView img = (ImageView)findViewById(R.id.segway_body);
                    img.setImageDrawable(bmd);
                }
            });
        /* ---------------------------------- */


        /* ATTACHTING THE FLING BLOCKS */
            // Initialize the fling gesture detectors
            _gestureDetectorBlock1 = new GestureDetector(new FlingDetector(View.this, (ViewFlipper)findViewById(R.id.block1_flipper)));
            _gestureDetectorBlock2 = new GestureDetector(new FlingDetector(View.this, (ViewFlipper)findViewById(R.id.block2_flipper)));

            // Bind the gesture detectors to the on touch events of the gesture overlays
            ((GestureOverlayView)findViewById(R.id.block1)).setOnTouchListener(new OnTouchListener()
            {
                public boolean onTouch(android.view.View v, MotionEvent event)
                {
                    if (_gestureDetectorBlock1.onTouchEvent(event))
                        return true;
                    else
                        return false;
                }
            });

            ((GestureOverlayView)findViewById(R.id.block2)).setOnTouchListener(new OnTouchListener()
            {
                public boolean onTouch(android.view.View v, MotionEvent event)
                {
                    if (_gestureDetectorBlock2.onTouchEvent(event))
                        return true;
                    else
                        return false;
                }
            });
       /* --------------------------- */
    }
}