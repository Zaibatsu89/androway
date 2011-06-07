package proj.androway.session;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import proj.androway.R;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.ConnectingBluetoothFailedException;
import proj.androway.common.Exceptions.ConstructingLoggingManagerFailedException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.connection.bluetooth.BluetoothManager;
import proj.androway.connection.bluetooth.BluetoothManager.ReceivedDataListener;
import proj.androway.connection.ConnectionFactory;
import proj.androway.connection.ConnectionManagerBase;
import proj.androway.connection.http.HttpManager;
import proj.androway.connection.IConnectionManager;
import proj.androway.database.DatabaseManagerBase;
import proj.androway.logging.LoggingManager;
import proj.androway.ui.RunningSessionView;

/**
 * The Controller class represents a session with the remote Androway. This class
 * is used for starting, controlling and stopping the session with the Androway.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class Session implements Runnable
{
    /**
     * The user id of the user running the current session
     */
    public int userId;

    /**
     * The session id of the current session
     */
    public int sessionId;

    private Context _context;
    private SharedObjects _sharedObjects;
    private SessionService _sessionService;
    private boolean _running = true;
    private LoggingManager _lm;
    private IConnectionManager _btManager;
    private Timer _updateTimer;
    private boolean _saveLog = true;

    /**
     * The constructor for the session. Initialize the session.
     * @param context       The application context
     * @param sharedObjects An instance of the common SharedObjects object
     */
    public Session(Context context, SharedObjects sharedObjects)
    {
        _context = context;
        _sharedObjects = sharedObjects;

        // Init the session id and the user id to -1, so we can check if it is an actual id or not.
        sessionId = -1;
        userId = -1;
    }

    public void run()
    {
        while(_running){}
    }

    /**
     * Start the session
     * @param sessionService    A reference to the SessionService that created this session
     * @return An integer array with the following format:
     * <pre>
     * [0] = successfullyStarted (value 0 || 1)
     * [1] = dialogType (value can be either RunningSessionView.DIALOG_TYPE_BLUETOOTH || RunningSessionView.DIALOG_TYPE_DONE || RunningSessionView.DIALOG_TYPE_FAILED || RunningSessionView.DIALOG_TYPE_START)
     * [2] = message (The string resource id. R.string.your_message_key
     * </pre>
     */
    public synchronized int[] startSession(SessionService sessionService)
    {
        _sessionService = sessionService;
        boolean startedSuccesful = false;
        int successfullyStarted = 0;
        int dialogType = -1;
        int message = -1;

        try
        {
            // Try to setup the logging manager (if the log type is Http, it will also try to login)
            // If it fails, it will throw an exception. The login failing will be handled in the catch block.
            _lm = new LoggingManager(_sharedObjects, _context, Settings.LOG_TYPE);

            // If the LoggingManager was succesfully created, and the log type is TYPE_HTTP,
            // get and store the sessionId and the userId.
            if(Settings.LOG_TYPE.equals(DatabaseManagerBase.TYPE_HTTP))
            {
                sessionId = HttpManager.sessionId;
                userId = HttpManager.userId;
            }

            // If the login process is done, wait for 1.5 seconds (otherwise it is going to fast)
            long endTime = System.currentTimeMillis() + 1500;
            while (System.currentTimeMillis() < endTime)
            {
                try { wait(endTime - System.currentTimeMillis()); }catch (Exception e) { }
            }

            // The login succeeded (because we reached this line) so change the message to bluetooth connecting.
            _sessionService.sendMessage(SessionService.MSG_UPDATE_DIALOG, RunningSessionView.DIALOG_TYPE_BLUETOOTH);

            // Try to open a connection with the given bluetooth address. If it fails, throw an exception.
            // The connection failing will be handled in the catch block.
            _btManager = ConnectionFactory.acquireConnectionManager(_context, ConnectionManagerBase.TYPE_BLUETOOTH);

            if(!_btManager.open("00:0b:53:13:20:c9")) //Settings.BLUETOOTH_ADDRESS
                throw new ConnectingBluetoothFailedException(_context.getString(R.string.ConnectingBluetoothFailedException));

            // Register the handleBluetoothReceived as onBluetoothReceivedData listener (callback function)
            ((BluetoothManager)_btManager).onBluetoothReceivedData(new ReceivedDataListener()
            {
                public void handleData(ArrayList data) { handleBluetoothReceived(data); }
            });

            // Set the dialog type to trigger when done
            dialogType = RunningSessionView.DIALOG_TYPE_DONE;

            startedSuccesful = true;
        }
        catch (ConstructingLoggingManagerFailedException ex)
        {
            // The login process failed, handle it
            long endTime = System.currentTimeMillis() + 3 * 1000;
            while (System.currentTimeMillis() < endTime)
            {
                try { wait(endTime - System.currentTimeMillis()); }catch (Exception e) { }
            }

            // Set the dialog type and its message to trigger when done
            dialogType = RunningSessionView.DIALOG_TYPE_FAILED;
            message = R.string.login_failed_message;

            startedSuccesful = false;
        }
        catch (ConnectingBluetoothFailedException ex)
        {
            // The bluetooth failed to connect to the device so handle it
            long endTime = System.currentTimeMillis() + 3 * 1000;
            while (System.currentTimeMillis() < endTime)
            {
                try
                {
                    wait(endTime - System.currentTimeMillis());
                }catch (Exception e) { }
            }

            // Set the dialog type and its message to trigger when done
            dialogType = RunningSessionView.DIALOG_TYPE_FAILED;
            message = R.string.bluetooth_failed_message;

            startedSuccesful = false;
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(startedSuccesful)
        {
            Settings.putSetting("sessionRunning", true);

            // Create a new timer, and schedule to execute the SendUpdateTask every UPDATE_INTERVAL
            _updateTimer = new Timer();
            _updateTimer.scheduleAtFixedRate(new SendUpdateTask(), 0, Constants.BT_UPDATE_INTERVAL);

            // The session was successfully started
            successfullyStarted = 1;
        }

        // Return an int array with if the login was succesfull, the dialogType and the optional message
        return new int[]{successfullyStarted, dialogType, message};
    }

    /**
     * Post (send) the default defined bluetooth message
     */
    public synchronized void bluetoothPost()
    {
        // Execute/send the standard update message
        new SendUpdateTask().run();
    }

    /**
     * Post (send) the given data through bluetooth
     * @param btData    The data to send
     */
    public synchronized void bluetoothPost(String btData)
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("bluetoothData", btData));

        if(_btManager != null && _btManager.checkConnection())
            _btManager.post("", data);
    }

    /**
     * The function that is triggered by the event handler for received bluetooth data
     * @param values    The received data
     */
    private synchronized void handleBluetoothReceived(ArrayList values)
    {
        if(values.size() > 0)
        {
            _sharedObjects.incomingData.leftWheelSpeed = Integer.parseInt((String)values.get(0));
            _sharedObjects.incomingData.rightWheelSpeed = Integer.parseInt((String)values.get(1));
            _sharedObjects.incomingData.inclination = Float.parseFloat((String)values.get(2));
            _sharedObjects.incomingData.batteryVoltage = Integer.parseInt((String)values.get(3));

            _sessionService.sendMessage(SessionService.MSG_UPDATE_SESSION_VIEWS, RunningSessionView.DIALOG_TYPE_BLUETOOTH);
        }

        // New data was set, so create a new log (1 of 2 times)
        if(_saveLog)
        {
            _lm.addLog();
            _saveLog = false;
        }
        else
            _saveLog = true;
    }

    /**
     * Stop the current session
     */
    public synchronized void stopSession()
    {
        // This session failed to start, so if a session was created in the remote DB. Delete it.
        if(Settings.START_SESSION_FAILED)
        {
            // Set the start session failed flag to false, because we handled it
            Settings.START_SESSION_FAILED = false;
            
            if(_lm != null)
                _lm.destroyFailedSession(sessionId, userId);
        }

        // Set the stopSession property to 1 (true), so the bot will properly stop.
        // Then perform a bluetoothPost, to send the last update message.
        _sharedObjects.outgoingData.stopSession = 1;

        bluetoothPost();

        // Set the session running setting to false
        Settings.putSetting("sessionRunning", false);
        _running = false;
    }

    /**
     * The SendUpdateTask class for sending the default update message through bluetooth
     */
    class SendUpdateTask extends TimerTask
    {
        public void run()
        {
            // Assemble the message
            String valuesToSend = "";
            valuesToSend += Float.toString(_sharedObjects.outgoingData.drivingDirection) + ",";
            valuesToSend += Float.toString(_sharedObjects.outgoingData.drivingSpeed) + ",";
            valuesToSend += Short.toString(_sharedObjects.outgoingData.do360) + ",";
            valuesToSend += Short.toString(_sharedObjects.outgoingData.onHold) + ",";
            valuesToSend += Short.toString(_sharedObjects.outgoingData.stopSession);

            // Post (send) the assembled message to the Androway
            bluetoothPost(valuesToSend);
        }
    }
}