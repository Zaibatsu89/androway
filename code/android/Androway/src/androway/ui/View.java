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
import android.widget.Toast;
import android.widget.ViewFlipper;
import androway.common.Exceptions.MapIsEmptyException;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.common.Settings;
import androway.logging.LoggingManager;
import androway.main.TiltControls;
import androway.ui.block_component.balance_block.BalanceBlock;
import androway.ui.block_component.BlockComponent;
import androway.ui.block_component.CompassBlock;
import androway.ui.block_component.InclinationBlock;
import androway.ui.quick_action.ActionItem;
import androway.ui.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main view of the system
 * @author Tymen en Rinse
 * @since 21-03-2011
 * @version 0.43
 */
public class View extends Activity
{
    private LoggingManager _lm;

    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;

    private Map<String, BlockComponent> _blockComponents = new HashMap<String, BlockComponent>();
    
    public int tempInclinationRotation = 0;
    private TiltControls _tempTiltControls;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        // Set the block components
        this.setBlockComponents();

        try
        {
            _lm = new LoggingManager(this.getBaseContext(), Settings.LOG_TYPE);
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
					Map<String, Object> dataMap = new HashMap<String, Object>();
					try {
						dataMap = _lm.getLogs();
					} catch (MapIsEmptyException ex) {
						Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
					}

					if(dataMap.isEmpty())
					{
						// Show empty toast
						Toast.makeText(View.this, getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
					}
					else
					{
						// Loop dataMap
						for (int i = 0; i < dataMap.size(); i++)
						{
							Map<String, Object> rowMap = (Map<String, Object>) dataMap.get("row" + i);

							Toast.makeText(View.this,
							"id: " + rowMap.get("id") +
							"\ntime: " + rowMap.get("time") +
							"\nsubject: " + rowMap.get("subject") +
							"\nmessage: " + rowMap.get("message"),
							Toast.LENGTH_LONG).show();
							}
					}
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

       // Bind the tilt controls
       _tempTiltControls = new TiltControls(View.this, this);
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

    private void setBlockComponents()
    {
        if(_blockComponents.isEmpty())
        {
            // Add all wanted BlockComponents
            _blockComponents.put(InclinationBlock.BLOCK_NAME, new InclinationBlock(View.this, R.layout.inclination));
            _blockComponents.put(BalanceBlock.BLOCK_NAME, new BalanceBlock(View.this, R.layout.balance));
            _blockComponents.put(CompassBlock.BLOCK_NAME, new CompassBlock(View.this, R.layout.compass));

            ViewFlipper block_1 = (ViewFlipper)findViewById(BlockComponent.ID_BLOCK_1);
            ViewFlipper block_2 = (ViewFlipper)findViewById(BlockComponent.ID_BLOCK_2);

            // Loop the components and add each component as view to the according block
            for(BlockComponent component : _blockComponents.values())
            {
                if(component.blockId == BlockComponent.ID_BLOCK_1)
                    block_1.addView(component);
                else if(component.blockId == BlockComponent.ID_BLOCK_2)
                    block_2.addView(component);
            }
        }
    }

    public void updateTiltViews(float azimuth, float pitch, float roll)
    {
        // Convert the sensor values to the actual speed and direction values
        float speed = (pitch * 2.222f) + 200;
        float direction = roll * -2.222f;

        // Limit the maximum speed value
        if(speed > 100)
            speed = 100;
        else if(speed < -100)
            speed = -100;

        // Limit the maximum direction value
        if(direction > 100)
            direction = 100;
        else if(direction < -100)
            direction = -100;

        // Set the new values
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("speed", speed);
        values.put("direction", direction);
        values.put("heading", azimuth);

        // Call the updateView function for all block components
        for(BlockComponent component : _blockComponents.values())
            component.updateView(BlockComponent.UPDATE_TYPE_TILT, values);
    }
}