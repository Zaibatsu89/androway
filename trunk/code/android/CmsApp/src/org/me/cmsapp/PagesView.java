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
public class PagesView extends TabActivity
{
    private Context context = this;
    private TabHost tabHost;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_tabs);

        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(context, P_Pages.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("p_pages")
                      .setIndicator("Pages", res.getDrawable(R.drawable.p_pages))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the following tabs
        intent = new Intent().setClass(context, P_General.class);
        spec = tabHost.newTabSpec("p_general")
                      .setIndicator("General", res.getDrawable(R.drawable.p_general))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(context, P_Content.class);
        spec = tabHost.newTabSpec("p_content")
                      .setIndicator("Content", res.getDrawable(R.drawable.p_content))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Set current (active) tab
        tabHost.setCurrentTabByTag("p_pages");
    }

    /* Creates the menu items */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pages_menu, menu);
        return true;
    }

    /* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;  // Reusable Intent for each optoin
        
        switch(item.getItemId())
        {
            case R.id.pages_menu_users:
            {
                // Launch the UsersView activity, and shut the current activity down
                intent = new Intent().setClass(context, UsersView.class);
                startActivityForResult(intent, 0);
                finish();
                
                return true;
            }
            case R.id.pages_menu_return:
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