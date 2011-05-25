/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import proj.androway.connection.ConnectionFactory;
import proj.androway.connection.ConnectionManagerBase;
import proj.androway.connection.IConnectionManager;
import proj.androway.logging.LoggingManager;
import proj.androway.main.IncomingData;
import proj.androway.main.OutgoingData;
import proj.androway.ui.RunningSessionView;

/**
 *
 * @author Tymen
 */
public class Session implements Runnable
{
    public static final int MSG_SET_VIEW = 0;
    public static final int MSG_SET_VALUE = 1;
    public static final int MSG_UPDATE_DIALOG = 2;
    public static final int MSG_BLUETOOTH_POST = 3;
    public static final int MSG_UPDATE_SESSION_VIEWS = 4;

    public static final String MSG_DATA_KEY = "data";

    private Context _context;
    private SharedObjects _sharedObjects;
    private SessionService _sessionService;
    private boolean _running = true;
    private LoggingManager _lm;
    private IConnectionManager _btManager;

    public int userId;
    public int sessionId;

    public Session(Context context, SharedObjects sharedObjects)
    {
        _context = context;
        _sharedObjects = sharedObjects;
    }

    public void run()
    {
        while(_running){}
    }

    /*
     * The logic for starting the Androway session
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

            // If the login process is done, wait for two more seconds (otherwise it is going to fast)
            long endTime = System.currentTimeMillis() + 2 * 1000;
            while (System.currentTimeMillis() < endTime)
            {
                try { wait(endTime - System.currentTimeMillis()); }catch (Exception e) { }
            }

            // The login succeeded (because we reached this line) so change the message to bluetooth connecting.
            _sessionService.sendMessage(Session.MSG_UPDATE_DIALOG, RunningSessionView.DIALOG_TYPE_BLUETOOTH);

            // Try to open a connection with the given bluetooth address. If it fails, throw an exception.
            // The connection failing will be handled in the catch block.
            _btManager = ConnectionFactory.acquireConnectionManager(_sharedObjects, _context, ConnectionManagerBase.TYPE_BLUETOOTH);

            if(!_btManager.open("00:0b:53:13:20:c9")) //Settings.BLUETOOTH_ADDRESS
                throw new ConnectingBluetoothFailedException(_context.getString(R.string.ConnectingBluetoothFailedException));

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
            _sharedObjects.updateTimer = new Timer();
            _sharedObjects.updateTimer.scheduleAtFixedRate(new SendUpdateTask(), 0, Constants.BT_UPDATE_INTERVAL);

            // The session was successfully started
            successfullyStarted = 1;
        }

        // Return an int array with if the login was succesfull, the dialogType and the optional message
        return new int[]{successfullyStarted, dialogType, message};
    }

    public synchronized void bluetoothPost(String btData)
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();        
        data.add(new BasicNameValuePair("bluetoothData", btData));

        if(_btManager != null)
            _btManager.post("", data);
    }

    public synchronized void handleBluetoothReceived(ArrayList values)
    {
        if(values.size() > 0)
        {
            _sharedObjects.incomingData.leftWheelSpeed = Integer.parseInt((String)values.get(0));
            _sharedObjects.incomingData.rightWheelSpeed = Integer.parseInt((String)values.get(1));
            _sharedObjects.incomingData.inclination = Float.parseFloat((String)values.get(2));

            _sessionService.sendMessage(Session.MSG_UPDATE_SESSION_VIEWS, RunningSessionView.DIALOG_TYPE_BLUETOOTH);
        }
        /*
        try
        {
            _lm.addLog();
        }
        catch (NotSupportedQueryTypeException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    /*
     * The logic for stopping the session
     */
    public synchronized void stopSession()
    {
        // Set the stopSession property to 1 (true), so the bot will properly stop.
        // Then perform a SendUpdateTask, to send the last update message.
        _sharedObjects.outgoingData.stopSession = 1;

        // If the bluetooth is connected, send a stop message
        if(_btManager != null && _btManager.checkConnection())
            new SendUpdateTask().run();

        // Set the session running setting to false
        Settings.putSetting("sessionRunning", false);
        _running = false;
    }

    // The task class for updating the data through bluetooth to the remote bot
    class SendUpdateTask extends TimerTask
    {
        public void run()
        {
            String valuesToSend = "";
            valuesToSend += Float.toString(_sharedObjects.outgoingData.drivingDirection) + ",";
            valuesToSend += Float.toString(_sharedObjects.outgoingData.drivingSpeed) + ",";
            valuesToSend += Short.toString(_sharedObjects.outgoingData.onHold) + ",";
            valuesToSend += Short.toString(_sharedObjects.outgoingData.stopSession);

            bluetoothPost(valuesToSend);
        }
    }
}