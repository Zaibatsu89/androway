/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proj.androway.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import proj.androway.common.Settings;
import proj.androway.ui.View;

/**
 *
 * @author Tymen
 */
public class ActivityBase extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Settings.initSettings(this.getApplicationContext());
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Settings.initSettings(this.getApplicationContext());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // When going into pause, store this Activity class name as the last active Activity
        Settings.putSetting("lastActivity", getClass().getName());
    }

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

        this.startActivity(new Intent(this.getApplicationContext(), returnTo));

        return;
    }
}
