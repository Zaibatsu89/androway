package proj.androway.main;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.R;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.ConstructingLoggingManagerFailedException;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.logging.LoggingManager;
import proj.androway.ui.RunningSessionView;
import proj.androway.ui.View;

/**
 *
 * @author Tymen
 */
public class Session extends Service
{
    private IBinder _binder = new SessionBinder();
    private RunningSessionView _sessionView;
    private SharedObjects _sharedObjects;
    private LoggingManager _lm;

    @Override
    public void onCreate()
    {
        _sharedObjects = (SharedObjects)this.getApplication();
    }

    // Called when the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The whole try-catch block is used to start the session as a foreground service
        try
        {
            // Get the notification strings
            String ticker = getString(R.string.start_session_ticker);
            String title = getString(R.string.start_session_title);
            String message = getString(R.string.start_session_message);

            Method startForegroundCall = getClass().getMethod("startForeground", new Class[] {int.class, Notification.class});
            Object[] startForegroundArgs = { Constants.NOTIFICATION_ID, getNotification(ticker, title, message) };
            startForegroundCall.invoke(this, startForegroundArgs);
        }
        catch (NoSuchMethodException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (SecurityException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (InvocationTargetException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (IllegalAccessException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }

        // Start the session
        _startSession();
        
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    // The service is no longer used and is being destroyed. Close down the session.
    @Override
    public void onDestroy()
    {
        // Stop the session
        _stopSession();
        
        // The whole try-catch block is used to remove the session as a foreground service
        try
        {
            // Stop foreground and make sure our notification is gone.
            Method stopForegroundCall = getClass().getMethod("stopForeground", new Class[] { boolean.class });
            Object[] stopForegroundArgs = { Boolean.TRUE };
            stopForegroundCall.invoke(this, stopForegroundArgs);
        }
        catch (NoSuchMethodException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (SecurityException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (InvocationTargetException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
        catch (IllegalAccessException ex) { Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex); }
    }

    // To communicate (bind) with the service, return the binder interface IBinder
    @Override
    public IBinder onBind(Intent intent)
    {
        return _binder;
    }

    /*
     * The logic for starting the Androway session
     */
    private void _startSession()
    {
        // Create the logging manager
        try
        {
            _lm = new LoggingManager(Session.this, Settings.LOG_TYPE);
            //_sessionView.updateProcessDialog(RunningSessionView.DIALOG_TYPE_DONE);
            System.out.println("success");
            System.out.println("success");
            System.out.println("success");
        }
        catch (ConstructingLoggingManagerFailedException ex)
        {
            //_sessionView.updateProcessDialog(RunningSessionView.DIALOG_TYPE_FAILED);
            System.out.println("failed");
            System.out.println("failed");
            System.out.println("failed");
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set the session running setting to true
        Settings.putSetting("sessionRunning", true);
    }

    /*
     * The logic for stopping the session
     */
    private void _stopSession()
    {
        // Set the session running setting to false
        Settings.putSetting("sessionRunning", false);
    }

    public Notification getNotification(String tickerText, String title, String message)
    {
        // Create the notification intent
        Intent notificationIntent = new Intent(this, Controller.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Set the RunningSessionView activity notification
        Notification notification = new Notification(R.drawable.notification_icon, tickerText, System.currentTimeMillis());
        notification.setLatestEventInfo(Session.this, title, message, contentIntent);
        notification.flags += Notification.FLAG_NO_CLEAR;
        notification.flags += Notification.FLAG_ONGOING_EVENT;

        return notification;
    }



// Three temporary log functions. Should be implemented when a new BT message is received.
public void tempAddLog()
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
public void tempGetLogs()
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
        Toast.makeText(Session.this, Session.this.getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
    }
    else
    {
        // Loop dataMap
        for (int i = 0; i < dataMap.size(); i++)
        {
            Map<String, Object> rowMap = (Map<String, Object>) dataMap.get("row" + i);

            Toast.makeText(Session.this,
            "id: " + rowMap.get("id") +
            "\ntime: " + rowMap.get("time") +
            "\nsubject: " + rowMap.get("subject") +
            "\nmessage: " + rowMap.get("message"),
            Toast.LENGTH_LONG).show();
        }
    }
}
public void tempClearLogs()
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

    /**
     * Class used for binding an activity to the session service.
     * Because we know this service always runs in the same process
     * as its clients, we don't need to deal with IPC.
     */
    public class SessionBinder extends Binder
    {
        public Session getService(RunningSessionView sessionView)
        {
            // Store the passed RunningSessionView, so we can interact with it.
            _sessionView = sessionView;

            // Return this instance of Session so clients can access public methods
            return Session.this;
        }
    }
}