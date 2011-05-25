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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import java.util.ArrayList;
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
import java.util.List;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;
import proj.androway.database.DatabaseManagerBase;
import proj.androway.main.IncomingData;
import proj.androway.main.OutgoingData;
import proj.androway.session.Session;
import proj.androway.session.SessionService;
import proj.androway.ui.block_component.DirectionBlock;
import proj.androway.ui.quick_action.ActionItem;
import proj.androway.ui.quick_action.QuickAction;

/**
 * The view for a running session
 * @author Tymen en Rinse
 * @since 04-04-2011
 * @version 0.21
 */
public class RunningSessionView extends ActivityBase
{
    public static final int DIALOG_TYPE_START = 0;
    public static final int DIALOG_TYPE_BLUETOOTH = 1;
    public static final int DIALOG_TYPE_DONE = 2;
    public static final int DIALOG_TYPE_FAILED = 3;

    public static final int STATUS_LOG_TYPE = 0;
    public static final int STATUS_BATTERY = 1;
    public static final int STATUS_BLUETOOTH = 2;

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
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Request the orientation that is stored in the settings
        this.setRequestedOrientation(Settings.DEVICE_ORIENTATION);

        _sharedObjects = (SharedObjects)this.getApplication();
        _sharedObjects.runningSessionView = this;
        _sharedObjects.incomingData = new IncomingData();
        _sharedObjects.outgoingData = new OutgoingData();

        /*
         * Create a screen bright wake-lock so that the screen stays on,
         * since the user will not be using the screen or buttons much.
         */
        _wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

        // Set the content for the view
        this.setContentView(R.layout.running_session);

        // Show the session startup dialog
        if(!Settings.SESSION_RUNNING)
            this.updateProcessDialog(DIALOG_TYPE_START, 0);
        else
            this.initView();
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
            this.updateProcessDialog(RunningSessionView.DIALOG_TYPE_FAILED, R.string.left_during_login);

        if(_accTiltControls != null)
            _accTiltControls.register();

        if(_oriTiltControls != null)
            _oriTiltControls.register();

        // When resuming the activity, acquire a wake-lock again
        _wakeLock.acquire();

        // Check and set the block status
        _setBlockStatus();

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
        _sharedObjects.runningSessionView = null;
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
                Message msg = Message.obtain(null, Session.MSG_SET_VIEW);
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
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Session.MSG_UPDATE_DIALOG:
                {
                    updateProcessDialog(msg.arg1, msg.arg2);
                    break;
                }
                case Session.MSG_UPDATE_SESSION_VIEWS:
                {
                    updateSessionDataViews();
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

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
                               _stopSession();
                           }
                       });

                _failedAlert = builder.create();
                _failedAlert.show();

                break;
            }
        }
    }

    public void initView()
    {
        // Set the block components
        _setBlockComponents();

        // Update the log type
        _updateStatusItem(STATUS_LOG_TYPE);

        // Bind the tilt controls for both the accelerometer and the orientation sensor
        _accTiltControls = new TiltControls(RunningSessionView.this, this, Sensor.TYPE_ACCELEROMETER);
        _oriTiltControls = new TiltControls(RunningSessionView.this, this, Sensor.TYPE_ORIENTATION);

        // Register the actual sensor
        _accTiltControls.register();
        _oriTiltControls.register();

        // EXAMPLE QUICK ACTION ITEMS
        final List<ActionItem> actionItems = new ArrayList<ActionItem>();
        ActionItem connect = new ActionItem();
        connect.setTitle(getString(R.string.add));
        connect.setIcon(getResources().getDrawable(R.drawable.bt_connect_icon));
        connect.setOnClickListener(new OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                // Send a message to link this view to the session service.
                Message msg = Message.obtain(null, Session.MSG_BLUETOOTH_POST);
                msg.replyTo = _messenger;
                Bundle bundle = new Bundle();
                bundle.putString(Session.MSG_DATA_KEY, "0,0,1");
                msg.setData(bundle);

                try
                {
                    // Send the data to the session connection, which will send it through bluetooth
                    _sessionConnection.send(msg);
                }
                catch (RemoteException ex)
                {
                    Logger.getLogger(RunningSessionView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        actionItems.add(connect);
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
        // ----------------------------------






        
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
            {
                // Launch the SettingsView activity
                this.startActivity(new Intent().setClass(this, SettingsView.class));

                return true;
            }
            case R.id.menu_quit_session:
            {
                this._stopSession();

                return true;
            }
        }

        return false;
    }

    private void _stopSession()
    {
        _sharedObjects.controller.stopSession();

        // Set running session to false (also done in stopService, but done here so that it is
        // done in time, because of main view is loaded)
        Settings.putSetting("sessionRunning", false);
        this.finish();
    }

    /*
     * Add the block components to their appropriate holder
     */
    private void _setBlockComponents()
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

    private void _updateBlockComponents(String updateType, Map<String, Object> data)
    {
        // Call the updateView function for all block components and pass the data
        for(BlockComponent component : _blockComponents.values())
            component.updateView(updateType, data);
    }

    /*
     * Update the status of the block locked: true/false by changing the icon
     */
    private void _setBlockStatus()
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

    private void _updateStatusItem(int statusItem)
    {
        _updateStatusItem(statusItem, new HashMap<String, Object>());
    }

    private void _updateStatusItem(int statusItem, Map<String, Object> params)
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

                break;
            }
            case STATUS_BATTERY:
            {
                // Change the battery icon based on the current value
                int batteryPower = (Integer)params.get("batteryPower");
                ImageButton batteryButton = (ImageButton) findViewById(R.id.log_web_button);

                if(batteryPower > 75)
                    batteryButton.setImageResource(R.drawable.battery_icon_100);
                else if(batteryPower > 50)
                    batteryButton.setImageResource(R.drawable.battery_icon_75);
                else if(batteryPower > 25)
                    batteryButton.setImageResource(R.drawable.battery_icon_50);
                else if(batteryPower > 10)
                    batteryButton.setImageResource(R.drawable.battery_icon_25);
                else if(batteryPower > 5)
                    batteryButton.setImageResource(R.drawable.battery_icon_10);
                else
                    batteryButton.setImageResource(R.drawable.battery_icon_5);

                break;
            }
            case STATUS_BLUETOOTH:
            {
                break;
            }
        }
    }

    /*
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
        _updateBlockComponents(BlockComponent.UPDATE_TYPE_TILT, values);
    }

    /*
     * Update the block components that require data from the current running session
     * with the connected Segway.
     */
    public void updateSessionDataViews()
    {
        // Update all block components with the new data
        _updateBlockComponents(BlockComponent.UPDATE_TYPE_SESSION_DATA, new HashMap<String, Object>());
    }
}