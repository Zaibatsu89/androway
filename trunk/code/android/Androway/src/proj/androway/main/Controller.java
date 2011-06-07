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
 * The Controller class is the center class of the application. This is the first Activity
 * that will always be called when the application starts. It will then decide what activity
 * needs to be launched.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
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
        launchLastStoredActivity();
        
        super.onStart();
    }

    /**
     * Launch the last avtive (stored) activity
     */
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

        // If the activity to start is the RunningSessionView and there is no session running, start the View instead.
        if(activityClass.equals(RunningSessionView.class) && !Settings.SESSION_RUNNING)
            activityClass = View.class;

        startActivity(new Intent(this, activityClass));
    }

    /**
     * Update the existing notification with the given information
     * @param ticker    The notification ticker text
     * @param title     The notification title
     * @param message   The notification message
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

    /**
     * Remove the existing notification
     */
    public void removeNotification()
    {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Constants.NOTIFICATION_ID);
    }

    /**
     * Start a new session and show the RunningSessionView (will just show the view if a session is allready running).
     * This also starts the foreground session service
     */
    public void runSession()
    {
        // Start the activity before the session is started!
        // Needed because of interaction from session with the RunningSessionView
        startActivity(new Intent(Controller.this, RunningSessionView.class));

        if(!Settings.SESSION_RUNNING)
            this.startService(new Intent(this, SessionService.class));
    }

    /**
     * Stop the currently running session and its foreground service
     */
    public void stopSession()
    {
        // Stop the session service and return to the main view
        this.stopService(new Intent(this, SessionService.class));
    }
}