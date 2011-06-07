package proj.androway.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.gesture.GestureOverlayView;
import android.graphics.Path.Direction;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.common.Settings;
import proj.androway.main.ActivityBase;
import proj.androway.main.TiltControls;
import proj.androway.ui.block_component.balance_block.BalanceBlock;
import proj.androway.ui.block_component.BlockComponent;
import proj.androway.ui.block_component.CompassBlock;
import proj.androway.ui.block_component.InclinationBlock;
import java.util.HashMap;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;
import proj.androway.database.DatabaseManagerBase;
import proj.androway.connection.bluetooth.IncomingData;
import proj.androway.connection.bluetooth.OutgoingData;
import proj.androway.session.SessionService;
import proj.androway.ui.block_component.DirectionBlock;

/**
 * The RunningSessionView class is the view for the running session
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class RunningSessionView extends ActivityBase
{
    /**
     * The dialog-update-type key for the starting of the session (if http it will be web login)
     */
    public static final int DIALOG_TYPE_START = 0;

    /**
     * The dialog-update-type key for the bluetooth connecting
     */
    public static final int DIALOG_TYPE_BLUETOOTH = 1;

    /**
     * The dialog-update-type key for when the dialog should close (session start is done)
     */
    public static final int DIALOG_TYPE_DONE = 2;

    /**
     * The dialog-update-type key for when an error occurred during the starting process
     */
    public static final int DIALOG_TYPE_FAILED = 3;

    /**
     * The statusbar update-type key for the logging type
     */
    public static final int STATUS_LOG_TYPE = 0;

    /**
     * The statusbar update-type key for the battery voltage
     */
    public static final int STATUS_BATTERY = 1;

    private SharedObjects _sharedObjects;
    private WakeLock _wakeLock;
    private GestureDetector _gestureDetectorBlock1;
    private GestureDetector _gestureDetectorBlock2;
    private TiltControls _accTiltControls;
    private TiltControls _oriTiltControls;
    private Map<String, BlockComponent> _blockComponents = new HashMap<String, BlockComponent>();
    private ProgressDialog _progressDialog = null;
    private AlertDialog _failedAlert = null;
    private boolean _startingSession = false;
    private boolean _pausedDuringLoginProcess = false;
    private boolean _pauseForBluetooth = false;
    private Messenger _sessionConnection = null;
    private final Messenger _messenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // If the orientation settings are set to landscape, hide the android status bar (fullscreen),
        // so we have more space for our 'dashboard'
        if(Settings.DEVICE_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Request the orientation that is stored in the settings
        this.setRequestedOrientation(Settings.DEVICE_ORIENTATION);

        _sharedObjects = (SharedObjects) getApplication();
        _sharedObjects.incomingData = new IncomingData();
        _sharedObjects.outgoingData = new OutgoingData();

         // Create a screen bright wake-lock so that the screen stays on,
         // since the user will not be using the screen or buttons much.
        _wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

        // Set the content for the view
        setContentView(R.layout.running_session);

        // Show the session startup dialog
        if(!Settings.SESSION_RUNNING)
            updateProcessDialog(DIALOG_TYPE_START, 0);
        else
            initView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Bind to SessionService service
        bindService(new Intent(this, SessionService.class), _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // If the user paused (left the activity) during the login process, show the failed dialog. Unable to handle.
        // Unless the bluetooth process has started
        if(_pausedDuringLoginProcess && !_pauseForBluetooth)
            updateProcessDialog(RunningSessionView.DIALOG_TYPE_FAILED, R.string.left_during_login);

        if(_accTiltControls != null)
            _accTiltControls.register();

        if(_oriTiltControls != null)
            _oriTiltControls.register();

        // When resuming the activity, acquire a wake-lock again
        _wakeLock.acquire();

        // Check and set the block status
        setBlockStatus();

        // Update the notification, whith the message that the session is running
        _sharedObjects.controller.updateNotification
        (
            getString(R.string.start_session_ticker),
            getString(R.string.start_session_title),
            getString(R.string.start_session_message)
        );
    }

    @Override
    protected void onPause()
    {
        // If the we where starting the session and the user is now pausing the activity
        // set the _pausedDuringLoginProcess to true, so we can show an alert on resume.
        if(_startingSession)
            _pausedDuringLoginProcess = true;

        // Release the wake-lock
        _wakeLock.release();

        if(_accTiltControls != null)
            _accTiltControls.unregister();

        if(_oriTiltControls != null)
            _oriTiltControls.unregister();       

        // If the bot is not on hold yet, put it on hold.
        if(_sharedObjects.outgoingData.onHold == 0)
        {
            try
            {
                // When leaving the app, set the onHold property to 1 (true) and send it
                // through bluetooth, so that the bot will also pause.
                _sharedObjects.outgoingData.onHold = 1;

                Message msg = Message.obtain(null, SessionService.MSG_BLUETOOTH_POST);
                msg.replyTo = _messenger;
                msg.arg1 = SessionService.BT_DATA_NOT_ATTACHED;

                _sessionConnection.send(msg);

                // Change the icon of the on hold image button to be active
                ((ImageButton) this.findViewById(R.id.on_hold_button)).setImageResource(R.drawable.on_hold_active_icon);
            }
            catch (RemoteException ex)
            {
                Logger.getLogger(RunningSessionView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Update the notification, whith the message that the session is on hold
        _sharedObjects.controller.updateNotification
        (
            getString(R.string.session_hold_ticker),
            getString(R.string.session_hold_title),
            getString(R.string.session_hold_message)
        );

        super.onPause();
    }

    @Override
    protected void onStop()
    {
        if(_accTiltControls != null)
            _accTiltControls.unregister();

        if(_oriTiltControls != null)
            _oriTiltControls.unregister();

        // Unbind from the SessionService service
        unbindService(_serviceConnection);

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        // Stop the notification for the running session
        _sharedObjects.controller.removeNotification();
        super.onDestroy();
    }

    // Defines callbacks for service binding, passed to the service
    private ServiceConnection _serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            try
            {
                // We've bound to the SessionService service, create a new messenger with the service.
                _sessionConnection = new Messenger(service);
                
                // Send a message to link this view to the session service.
                Message msg = Message.obtain(null, SessionService.MSG_SET_VIEW);
                msg.replyTo = _messenger;
                _sessionConnection.send(msg);
            }
            catch (RemoteException ex)
            {
                Logger.getLogger(RunningSessionView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void onServiceDisconnected(ComponentName arg0) { }
    };

    /**
     * Handler of incoming messages from the SessionService
     */
    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SessionService.MSG_UPDATE_DIALOG:
                {
                    updateProcessDialog(msg.arg1, msg.arg2);
                    break;
                }
                case SessionService.MSG_UPDATE_SESSION_VIEWS:
                {
                    updateSessionDataViews();
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Update the process dialog
     * @param processDialogType What dialog type to use (DIALOG_TYPE_'the dialog type')
     * @param messageId The resource id of the string (R.string.your_message_key)
     */
    public void updateProcessDialog(int processDialogType, int messageId)
    {
        if(_progressDialog == null)
            _progressDialog = new ProgressDialog(RunningSessionView.this);

        _progressDialog.setCancelable(false);

        switch(processDialogType)
        {
            case DIALOG_TYPE_START:
            {
                _startingSession = true;

                // If LOG_TYPE is http logging in is required, so show that message.
                // Otherwise continue with the bluetooth message.
                if(Settings.LOG_TYPE.equals(DatabaseManagerBase.TYPE_HTTP))
                {
                    String password = "";

                    int passwordLength = Settings.USER_PASSWORD.length();
                    for(int i = 0; i < passwordLength; i++)
                        password += "*";

                    _progressDialog.setTitle(R.string.login_title);
                    _progressDialog.setMessage(getString(R.string.login_message) + Settings.USER_EMAIL + "\n" + password);
                    _progressDialog.setIcon(R.drawable.ic_dialog_login);
                    _progressDialog.show();
                    break;
                }
                else
                {
                    // Change the progressDialogType to proceed to the bluetooth case
                    processDialogType = DIALOG_TYPE_BLUETOOTH;
                }
            }
            case DIALOG_TYPE_BLUETOOTH:
            {
                _pauseForBluetooth = true;
                
                _progressDialog.setTitle(R.string.bluetooth_title);
                _progressDialog.setMessage(getString(R.string.bluetooth_message) + Settings.BLUETOOTH_ADDRESS);
                _progressDialog.setIcon(R.drawable.ic_dialog_bluetooth);
                _progressDialog.show();
                break;
            }
            case DIALOG_TYPE_DONE:
            {
                this.initView();
                _startingSession = false;
                _progressDialog.dismiss();
                break;
            }
            case DIALOG_TYPE_FAILED:
            {
                _startingSession = false;

                if(_progressDialog != null && _progressDialog.isShowing())
                    _progressDialog.dismiss();

                int message = R.string.error_message;

                if(messageId > 0)
                    message = messageId;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.error_title)
                       .setMessage(message)
                       .setCancelable(false)
                       .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                       {
                           public void onClick(DialogInterface dialog, int id)
                           {
                               stopSession();
                           }
                       });

                Settings.START_SESSION_FAILED = true;
                _failedAlert = builder.create();
                _failedAlert.show();

                break;
            }
        }
    }

    /**
     * Initialize (create) the session view
     */
    public void initView()
    {
        // Set the block components
        setBlockComponents();

        // Update the log type
        updateStatusItem(STATUS_LOG_TYPE);

        // Bind the tilt controls for both the accelerometer and the orientation sensor
        _accTiltControls = new TiltControls(this, Sensor.TYPE_ACCELEROMETER);
        _oriTiltControls = new TiltControls(this, Sensor.TYPE_ORIENTATION);

        // Bind the onTiltSensorChanged listeners and let them trigger the updateTiltViews function
        _accTiltControls.onTiltSensorChanged(new TiltControls.TiltDataChangedListener()
        {
            public void updateTilt(float azimuth, float pitch, float roll, int sensorType)
            {
                updateTiltViews(azimuth, pitch, roll, sensorType);
            }
        });
        _oriTiltControls.onTiltSensorChanged(new TiltControls.TiltDataChangedListener()
        {
            public void updateTilt(float azimuth, float pitch, float roll, int sensorType)
            {
                updateTiltViews(azimuth, pitch, roll, sensorType);
            }
        });

        // Register the actual sensor
        _accTiltControls.register();
        _oriTiltControls.register();

        // Onclick listener for the battery voltage status bar button
        ImageButton batteryButton = (ImageButton) findViewById(R.id.battery_button);
        batteryButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {                
                // Show a toast with the current battery voltage
                Toast.makeText(RunningSessionView.this, _sharedObjects.incomingData.batteryVoltage + getString(R.string.battery_voltage_suffix), Toast.LENGTH_LONG).show();
            }
        });

        // Onclick listener for the on hold status bar button
        ImageButton onHoldButton = (ImageButton) this.findViewById(R.id.on_hold_button);
        onHoldButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                ImageButton onHoldButton = (ImageButton) findViewById(R.id.on_hold_button);

                // Toggle the do360 value and icon
                if(_sharedObjects.outgoingData.onHold == 1)
                {
                    _sharedObjects.outgoingData.onHold = 0;
                    onHoldButton.setImageResource(R.drawable.on_hold_icon);
                }
                else
                {
                    _sharedObjects.outgoingData.onHold = 1;
                    onHoldButton.setImageResource(R.drawable.on_hold_active_icon);
                }
            }
        });

        // Onclick listener for the do 360 status bar button
        ImageButton do360Button = (ImageButton) this.findViewById(R.id.do_360_button);
        do360Button.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                ImageButton do360Button = (ImageButton) findViewById(R.id.do_360_button);

                // Toggle the do360 value and icon
                if(_sharedObjects.outgoingData.do360 == 1)
                {
                    _sharedObjects.outgoingData.do360 = 0;
                    do360Button.setImageResource(R.drawable.do_360_icon);
                }
                else
                {
                    _sharedObjects.outgoingData.do360 = 1;
                    do360Button.setImageResource(R.drawable.do_360_active_icon);
                }
            }
        });
        
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
                // Launch the SettingsView activity
                this.startActivity(new Intent().setClass(this, SettingsView.class));
                return true;
            case R.id.menu_quit_session:
                this.stopSession();
                return true;
        }

        return false;
    }

    /**
     * Stop the currently running session and leave this view
     */
    private void stopSession()
    {
        _sharedObjects.controller.stopSession();

        // Set running session to false (also done in stopService, but done here so that it is
        // done in time, because of main view is loaded)
        Settings.putSetting("sessionRunning", false);
        this.finish();
    }

    /**
     * Add the block components to their appropriate holder
     */
    private void setBlockComponents()
    {
        if(_blockComponents.isEmpty())
        {
            // Add all wanted BlockComponents
            _blockComponents.put(InclinationBlock.class.getName(), new InclinationBlock(RunningSessionView.this, _sharedObjects, R.layout.inclination));
            _blockComponents.put(BalanceBlock.class.getName(), new BalanceBlock(RunningSessionView.this, _sharedObjects, R.layout.balance));
            _blockComponents.put(CompassBlock.class.getName(), new CompassBlock(RunningSessionView.this, _sharedObjects, R.layout.compass));
            _blockComponents.put(Direction.class.getName(), new DirectionBlock(RunningSessionView.this, _sharedObjects, R.layout.direction));

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

    /**
     * Update the block components with the given data
     * @param updateType    The update type (BlockComponent.UPDATE_TYPE_'your type')
     * @param data          The update data
     */
    private void updateBlockComponents(String updateType, Map<String, Object> data)
    {
        // Call the updateView function for all block components and pass the data
        for(BlockComponent component : _blockComponents.values())
            component.updateView(updateType, data);
    }

    /**
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

    /**
     * Trigger the update the given status item
     * @param statusItem    The status item (STATUS_'status item')
     */
    private void updateStatusItem(int statusItem)
    {
        updateStatusItem(statusItem, new HashMap<String, Object>());
    }

    /**
     * Trigger the update of the given status item
     * @param statusItem    The status item (STATUS_'status item')
     * @param params        The update parameters (data)
     */
    private void updateStatusItem(int statusItem, Map<String, Object> params)
    {
        switch(statusItem)
        {
            case STATUS_LOG_TYPE:
            {
                // Change the logging type icon based on the current setting
                ImageButton logTypeButton = (ImageButton) findViewById(R.id.log_web_button);

                if(Settings.LOG_TYPE.equals(DatabaseManagerBase.TYPE_HTTP))
                    logTypeButton.setImageResource(R.drawable.log_web_icon);
                else
                    logTypeButton.setImageResource(R.drawable.log_local_icon);

                // Attach the onlick listener. When the log button is clicked, a message
                // regarding the current log type will be shown.
                logTypeButton.setOnClickListener(new android.view.View.OnClickListener()
                {
                    public void onClick(android.view.View v)
                    {
                        int messageResourceId;

                        if(Settings.LOG_TYPE.equals(DatabaseManagerBase.TYPE_HTTP))
                            messageResourceId = R.string.log_online_toast;
                        else
                            messageResourceId = R.string.log_local_toast;

                        Toast.makeText(RunningSessionView.this, getString(messageResourceId), Toast.LENGTH_LONG).show();
                    }
                });

                break;
            }
            case STATUS_BATTERY:
            {
                // Change the battery icon based on the current value
                int batteryPower = _sharedObjects.incomingData.batteryVoltage;
                ImageButton batteryButton = (ImageButton) findViewById(R.id.battery_button);

                if(batteryPower > 87.5)
                    batteryButton.setImageResource(R.drawable.battery_icon_100);
                else if(batteryPower > 62.5)
                    batteryButton.setImageResource(R.drawable.battery_icon_75);
                else if(batteryPower > 37.5)
                    batteryButton.setImageResource(R.drawable.battery_icon_50);
                else if(batteryPower > 12.5)
                    batteryButton.setImageResource(R.drawable.battery_icon_25);
                else if(batteryPower > 6)
                    batteryButton.setImageResource(R.drawable.battery_icon_10);
                else if(batteryPower != -1)
                    batteryButton.setImageResource(R.drawable.battery_icon_5);

                break;
            }
        }
    }

    /**
     * Updates the block components with the new data from the tilt conroller
     */
    public void updateTiltViews(float azimuth, float pitch, float roll, int sensorType)
    {
        // Set the new values
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(TiltControls.UPDATE_AZIMUTH, azimuth);
        values.put(TiltControls.UPDATE_PITCH, pitch);
        values.put(TiltControls.UPDATE_ROLL, roll);
        values.put(TiltControls.UPDATE_SENSOR_TYPE, sensorType);

        // Update all block components with the new data
        updateBlockComponents(BlockComponent.UPDATE_TYPE_TILT, values);
    }

    /**
     * Update the block components that require data from the current running session
     * with the connected Androway.
     */
    public void updateSessionDataViews()
    {
        // Update all block components with the new data
        updateBlockComponents(BlockComponent.UPDATE_TYPE_SESSION_DATA, new HashMap<String, Object>());
        updateStatusItem(STATUS_BATTERY);
    }
}