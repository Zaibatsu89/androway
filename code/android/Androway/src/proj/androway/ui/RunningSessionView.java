package proj.androway.ui;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.common.Settings;
import proj.androway.logging.LoggingManager;
import proj.androway.main.ActivityBase;
import proj.androway.main.TiltControls;
import proj.androway.ui.block_component.balance_block.BalanceBlock;
import proj.androway.ui.block_component.BlockComponent;
import proj.androway.ui.block_component.CompassBlock;
import proj.androway.ui.block_component.InclinationBlock;
import proj.androway.ui.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.R;
import proj.androway.common.SharedObjects;
import proj.androway.main.Controller;
import proj.androway.ui.quick_action.ActionItem;

/**
 * The view for a running session
 * @author Tymen
 * @since 29-03-2011
 * @version 0.2
 */
public class RunningSessionView extends ActivityBase
{
    private SharedObjects _sharedObjects;
    private WakeLock _wakeLock;
    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;
    private TiltControls _tiltControls;
    private Map<String, BlockComponent> _blockComponents = new HashMap<String, BlockComponent>();

    public int tempInclinationRotation = 0;
    private LoggingManager _lm;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _sharedObjects = (SharedObjects)this.getApplication();

        /*
         * Create a screen bright wake-lock so that the screen stays on,
         * since the user will not be using the screen or buttons much.
         */
        _wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

        // Request the orientation that is stored in the settings
        this.setRequestedOrientation(Settings.DEVICE_ORIENTATION);

        // Set the content for the view
        this.setContentView(R.layout.running_session);

        // Set the block components
        this.setBlockComponents();

        // Bind the tilt controls, MOVE TO CONTROLLER
        _tiltControls = new TiltControls(RunningSessionView.this, this);

        // Create the logging manager, MOVE TO CONTROLLER
        try
        {
            _lm = new LoggingManager(this.getBaseContext(), Settings.LOG_TYPE);
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

        final List<ActionItem> actionItems = new ArrayList<ActionItem>();

        // EXAMPLE QUICK ACTION ITEMS WITH TEMP LOGGING AS ACTION
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
                    Map<String, Object> dataMap = new HashMap<String, Object>();

                    try
                    {
                        dataMap = _lm.getLogs();
                    }
                    catch (MapIsEmptyException ex)
                    {
                        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if(dataMap.isEmpty())
                    {
                        // Show empty toast
                        Toast.makeText(RunningSessionView.this, getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        // Loop dataMap
                        for (int i = 0; i < dataMap.size(); i++)
                        {
                            Map<String, Object> rowMap = (Map<String, Object>) dataMap.get("row" + i);

                            Toast.makeText(RunningSessionView.this,
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
                        _lm.clearAll();
                    }
                    catch (NotSupportedQueryTypeException ex)
                    {
                        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            actionItems.add(settings);
        // ------------------------------------------------------

        // STATUSBAR BUTTON ONCLICK LISTENERS
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
        // ----------------------------------


        // ATTACHTING THE FLING BLOCKS
            // Initialize the fling gesture detectors
            _gestureDetectorBlock1 = new GestureDetector(new FlingDetector(RunningSessionView.this, BlockComponent.ID_BLOCK_1, (ViewFlipper)findViewById(R.id.block1_flipper)));
            _gestureDetectorBlock2 = new GestureDetector(new FlingDetector(RunningSessionView.this, BlockComponent.ID_BLOCK_2, (ViewFlipper)findViewById(R.id.block2_flipper)));

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
       // ---------------------------
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(_tiltControls != null)
            _tiltControls.register();

        // When resuming the activity, acquire a wake-lock again
        _wakeLock.acquire();

        // Check and set the block status
        this.setBlockStatus();

        // Update the notification, whith the message that the session is running
        _sharedObjects.controller.setNotification(Controller.NOTIFICATION_ID, "Running session", "Select to view the running session.");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Release the wake-lock
        _wakeLock.release();

        if(_tiltControls != null)
            _tiltControls.unregister();

        _sharedObjects.controller.setNotification(Controller.NOTIFICATION_ID, "Session on hold", "Select to continue the running session.");
    }

    @Override
    protected void onStop()
    {
        if(_tiltControls != null)
            _tiltControls.unregister();

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        _sharedObjects.controller.removeNotification(Controller.NOTIFICATION_ID);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.running_session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_settings:
            {
                // Launch the SettingsView activity
                this.startActivity(new Intent().setClass(this, SettingsView.class));

                return true;
            }
            case R.id.menu_quit_session:
            {
                _sharedObjects.controller.stopSession();
                this.finish();

                return true;
            }
        }

        return false;
    }

    /*
     * Add the block components to their appropriate holder
     */
    private void setBlockComponents()
    {
        if(_blockComponents.isEmpty())
        {
            // Add all wanted BlockComponents
            _blockComponents.put(InclinationBlock.BLOCK_NAME, new InclinationBlock(RunningSessionView.this, R.layout.inclination));
            _blockComponents.put(BalanceBlock.BLOCK_NAME, new BalanceBlock(RunningSessionView.this, R.layout.balance));
            _blockComponents.put(CompassBlock.BLOCK_NAME, new CompassBlock(RunningSessionView.this, R.layout.compass));

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

    /*
     * Update the status of the block locked: true/false by changing the icon
     */
    private void setBlockStatus()
    {
        // The block icon ImageView
        ImageView blockStatusImg = (ImageView) findViewById(R.id.blocked_block);

        // Set the appropriate image based on the current settings
        if(Settings.BLOCK_1_LOCKED && Settings.BLOCK_2_LOCKED)
            blockStatusImg.setImageResource(R.drawable.first_second_locked);
        else if(Settings.BLOCK_1_LOCKED)
            blockStatusImg.setImageResource(R.drawable.first_locked);
        else if(Settings.BLOCK_2_LOCKED)
            blockStatusImg.setImageResource(R.drawable.second_locked);
        else
            blockStatusImg.setImageResource(R.drawable.transparent);
    }

    /*
     * Updates the block components with the new data from the tilt conroller
     * @param azimuth The orientation azimuth value
     * @param pitch The orientation pitch value
     * @param roll The orientation roll value
     */
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