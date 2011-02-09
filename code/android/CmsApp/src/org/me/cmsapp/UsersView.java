/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.cmsapp;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 *
 * @author Tymen
 */
public class UsersView extends TabActivity
{
    private Context context = this;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_tabs);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(context, U_Users.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("u_users")
                      .setIndicator("Users", res.getDrawable(R.drawable.u_users))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the second tab
        intent = new Intent().setClass(context, U_Information.class);
        spec = tabHost.newTabSpec("u_information")
                      .setIndicator("Information", res.getDrawable(R.drawable.u_information))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Set current (active) tab
        tabHost.setCurrentTabByTag("u_users");
    }

    /* Creates the menu items */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.users_menu, menu);
        return true;
    }

    /* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;  // Reusable Intent for each optoin

        switch(item.getItemId())
        {
            case R.id.users_menu_pages:
            {
                // Launch the UsersView activity, and shut the current activity down
                intent = new Intent().setClass(context, PagesView.class);
                startActivityForResult(intent, 0);
                finish();

                return true;
            }
            case R.id.users_menu_return:
            {
                // Shut down and return to main activity (main menu)
                setResult(RESULT_OK, new Intent());
                finish();

                return true;
            }
        }
        return false;
    }
}