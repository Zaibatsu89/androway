package proj.androway.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import proj.androway.common.Settings;
import proj.androway.ui.View;

/**
 * The ActivityBase class is a base class for the view Activities. It has some basic behavior
 * that is needed for all view Activities (f.e. it loads the application settings, handles
 * the last visited activity and overrides the back button behavior).
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class ActivityBase extends Activity
{
    /**
     * After creating the Activity (super) load the application settings
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // When creating the Activity, load the application settings
        Settings.initSettings(this.getBaseContext());
    }

    /**
     * After resuming the Activity (super) load the application settings
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // When resuming the Activity, load the application settings
        Settings.initSettings(this.getBaseContext());
    }

    /**
     * When pausing store this Activity's class name as the last active Activity
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        // When pausing, store this Activity class name as the last active Activity, so that
        // when we return we can show this Activity again.
        Settings.putSetting("lastActivity", getClass().getName());
    }

    /**
     * Custom back-button handling. Return to the last (stored) active Activity.
     */
    @Override
    public void onBackPressed()
    {
        String className = this.getClass().getName();
        Class<?> returnTo;

        // When the back button is pressed and the last activity setting is the class itself, return to view
        if(Settings.LAST_ACTIVITY.equals(className) && !className.equals(View.class.getName()))
            returnTo = View.class;
        else
        {
            try
            {
                returnTo = Class.forName(Settings.LAST_ACTIVITY);
            }
            catch (ClassNotFoundException ex)
            {
                returnTo = View.class;
            }
        }

        // Start the Activity
        this.startActivity(new Intent(this.getApplicationContext(), returnTo));

        return;
    }
}
