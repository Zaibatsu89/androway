package proj.androway.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import proj.androway.common.Settings;
import proj.androway.R;
import proj.androway.common.SharedObjects;
import proj.androway.ui.View;

/**
 * Class Controller connects with the following packages:
 * Common, Connection, Logging, UI and class TiltControls.
 * @author Rinse
 * @since 10-02-2011
 * @version 0.1
 */
public class Controller extends Activity
{
    public static final int NOTIFICATION_ID = 1;

    private SharedObjects _sharedObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        _sharedObjects = (SharedObjects)this.getApplication();
        _sharedObjects.controller = this;        
    }

    @Override
    protected void onStart()
    {
        // Load the application settings and put them in the variables
        Settings.initSettings(Controller.this);
        this.launchLastStoredActivity();
        
        super.onStart();
    }

    private void launchLastStoredActivity()
    {
        Class<?> activityClass;

        try
        {
            // Get the last stored activity from the settings
            activityClass = Class.forName(Settings.LAST_ACTIVITY);
        }
        catch(ClassNotFoundException ex)
        {
            // If getting the last stored activity fails, launch the View class
            activityClass = View.class;
        }

        startActivity(new Intent(this, activityClass));
    }

    public void setNotification(int id, String title, String message)
    {
        // Get the notification manager
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create the notification intent
        Intent notificationIntent = new Intent(this, Controller.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Set the RunningSessionView activity notification
        Notification notification = new Notification(R.drawable.notification_icon, title, System.currentTimeMillis());
        notification.setLatestEventInfo(Controller.this, title, message, contentIntent);
        notification.flags += Notification.FLAG_NO_CLEAR;
        notification.flags += Notification.FLAG_ONGOING_EVENT;

        // Attach/launch the notification
        mNotificationManager.notify(id, notification);
    }

    public void removeNotification(int id)
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    public void startSession()
    {
        if(!Settings.SESSION_RUNNING || _sharedObjects.session == null)
        {
            Session session = new Session(_sharedObjects);
            session.start();
            _sharedObjects.session = session;
        }
    }

    public void stopSession()
    {
        if(Settings.SESSION_RUNNING && _sharedObjects.session != null)
            _sharedObjects.session.stop();

        startActivity(new Intent(Controller.this, View.class));
    }
}