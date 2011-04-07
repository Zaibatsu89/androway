package proj.androway.main;

import proj.androway.session.SessionService;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import proj.androway.common.Settings;
import proj.androway.R;
import proj.androway.common.Constants;
import proj.androway.common.SharedObjects;
import proj.androway.ui.RunningSessionView;
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
        _launchLastStoredActivity();
        
        super.onStart();
    }

    private void _launchLastStoredActivity()
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

    /*
     * Update the existing session notification
     */
    public void updateNotification(String ticker, String title, String message)
    {
        // Create the notification intent
        Intent notificationIntent = new Intent(this, Controller.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Set the RunningSessionView activity notification
        Notification notification = new Notification(R.drawable.notification_icon, ticker, System.currentTimeMillis());
        notification.setLatestEventInfo(Controller.this, title, message, contentIntent);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(Constants.NOTIFICATION_ID, notification);
    }

    public void removeNotification()
    {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Constants.NOTIFICATION_ID);
    }

    public void runSession()
    {
        // Start the activity before the session is started!
        // Needed because of interaction from session with the RunningSessionView
        startActivity(new Intent(Controller.this, RunningSessionView.class));

        if(!Settings.SESSION_RUNNING)
            startService(new Intent(this, SessionService.class));
    }

    public void stopSession()
    {
        // Stop the session service and return to the main view
        this.stopService(new Intent(this, SessionService.class));
        this.startActivity(new Intent(Controller.this, View.class));
    }
}