package androway.ui;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.logging.LoggingManager;
import androway.main.TiltControls;
import androway.ui.quick_action.ActionItem;
import androway.ui.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main view of the system
 * @author Tymen en Rinse
 * @since 02-03-2011
 * @version 0.41
 */
public class View extends Activity
{
    private LoggingManager _lm;

    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;

    private BalanceView _balanceView;
    
    public int tempInclinationRotation = 0;
    private TiltControls _tempTiltControls;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        try
        {
            _lm = new LoggingManager(this);
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

        final List<ActionItem> actionItems = new ArrayList<ActionItem>();

        /* EXAMPLE QUICK ACTION ITEMS WITH TEMP LOGGING AS ACTION */
            ActionItem connect = new ActionItem();
            //connect.setTitle(this.getString(R.string.connect));
            connect.setTitle(getString(R.string.add));
            connect.setIcon(getResources().getDrawable(R.drawable.bt_connect_icon));
            connect.setOnClickListener(new OnClickListener()
            {
                public void onClick(android.view.View v)
                {
                    try
                    {
                        //Toast.makeText(View.this, "Connecting to the Segway", Toast.LENGTH_SHORT).show();
                        _lm.addLog("NHL Hogeschool", "Minor Androway");
                    }
                    catch (NotSupportedQueryTypeException ex)
                    {
                        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                    Map<String, ContentValues> dataMap = _lm.getLog(0);

                    String message = "";

                    for (String key : dataMap.keySet())
                    {
                        ContentValues cv = dataMap.get(key);
                        message = cv.getAsString(key);
                    }

                    if (!message.equals(""))
                    {
                        Toast.makeText(View.this,
                        getString(R.string.id) + "\n" + message,
                        Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(View.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
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
                    try
                    {
                        //Toast.makeText(View.this, "Go to Bluetooth settings", Toast.LENGTH_SHORT).show();
                        _lm.clearAll();
                    }
                    catch (NotSupportedQueryTypeException ex)
                    {
                        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                    }
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

                    // Create the matrix for the rotation of the bitmap
                    Matrix mtx = new Matrix();
                    mtx.postRotate(tempInclinationRotation);

                    // Rotating Bitmap
                    Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mtx, true);
                    BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);
                    
                    ImageView img = (ImageView)findViewById(R.id.segway_body);
                    img.setImageDrawable(bmd);

                    tempInclinationRotation += 5;
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



       // Add the BalanceView to the balance LinearLayout
       LinearLayout balanceWrapper = (LinearLayout) findViewById(R.id.balance);
       _balanceView = new BalanceView(View.this);
       _balanceView.setBackgroundDrawable(getResources().getDrawable(R.drawable.balance_bg));


       ImageView balanceScale = new ImageView(View.this);
       balanceScale.setImageResource(R.drawable.balance_scale);
       _balanceView.addView(balanceScale);


       balanceWrapper.addView(_balanceView);

       _tempTiltControls = new TiltControls(View.this, _balanceView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _tempTiltControls.register();
    }

    @Override
    protected void onStop()
    {
        _tempTiltControls.unregister();
        super.onStop();
    }
}