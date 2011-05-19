package proj.androway.session;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.R;
import proj.androway.common.Constants;
import proj.androway.main.Controller;

/**
 *
 * @author Tymen
 */
public class SessionService extends Service
{
    private Messenger _sessionViewConnection = null;
    private final Messenger _messenger = new Messenger(new IncomingHandler());

    private Thread _sessionThread;
    private Session _session;

    @Override
    public void onCreate()
    {
        _session = new Session(SessionService.this);
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
        catch (NoSuchMethodException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (SecurityException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (InvocationTargetException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (IllegalAccessException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }
        
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    // The service is no longer used and is being destroyed. Close down the session.
    @Override
    public void onDestroy()
    {
        // Stop the session
        _session.stopSession();
        
        // The whole try-catch block is used to remove the session as a foreground service
        try
        {
            // Stop foreground and make sure our notification is gone.
            Method stopForegroundCall = getClass().getMethod("stopForeground", new Class[] { boolean.class });
            Object[] stopForegroundArgs = { Boolean.TRUE };
            stopForegroundCall.invoke(this, stopForegroundArgs);
        }
        catch (NoSuchMethodException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (SecurityException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (InvocationTargetException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }        catch (IllegalAccessException ex) { Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex); }
    }

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Session.MSG_SET_VIEW:
                {
                    if(_sessionViewConnection == null)
                    {
                        _sessionViewConnection = msg.replyTo;

                        // Start the session start sequence from a thread
                        // so that the handleMessage is immediatly done and it will not block the UI thread
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                Looper.prepare();
                                _startSession();
                                Looper.loop();
                            }
                        }).start();
                    }

                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /*
     * Function that handles the starting of the session and handles the messaging back to the session view
     */
    public void _startSession()
    {
        // Put the actual session in a thread
        _sessionThread = new Thread(_session);
        _sessionThread.start();

        // When the binding process is complete (messagers bound) start the session.
        // The int array will contain the start results.
        // sessionStartResult[0] = whether the login was successfull
        // sessionStartResult[1] = the dialog type to show
        // sessionStartResult[2] = the dialog message
        int[] sessionStartResult = _session.startSession();

        // Check if the session start failed, if so stop it (kill the thread)
        if(sessionStartResult[0] == 0)
            _session.stopSession();

        // Send a message with the results to the RunningSessionView
        // with the returned dialog type and message.
        Message msg = Message.obtain(null, Session.MSG_UPDATE_DIALOG, sessionStartResult[1], sessionStartResult[2]);
        try
        {
            _sessionViewConnection.send(msg);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(SessionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // To communicate (bind) with the service, return the binder interface IBinder
    @Override
    public IBinder onBind(Intent intent)
    {
        return _messenger.getBinder();
    }

    public Notification getNotification(String tickerText, String title, String message)
    {
        // Create the notification intent
        Intent notificationIntent = new Intent(this, Controller.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Set the RunningSessionView activity notification
        Notification notification = new Notification(R.drawable.notification_icon, tickerText, System.currentTimeMillis());
        notification.setLatestEventInfo(SessionService.this, title, message, contentIntent);
        notification.flags += Notification.FLAG_NO_CLEAR;
        notification.flags += Notification.FLAG_ONGOING_EVENT;

        return notification;
    }
}