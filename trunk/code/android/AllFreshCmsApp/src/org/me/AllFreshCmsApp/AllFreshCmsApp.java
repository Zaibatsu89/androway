/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.AllFreshCmsApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *
 * @author Tymen
 */
public class AllFreshCmsApp extends Activity
{
    private Context context = this;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button mainMenuButton = (Button)findViewById(R.id.main_menu_button);

        mainMenuButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                openOptionsMenu();
            }
        });
    }

    /* Creates the menu items */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;  // Reusable Intent for each option
        
        switch(item.getItemId())
        {
            case R.id.main_menu_users:
            {
                // Launch the UsersTabs activity
                intent = new Intent().setClass(this, UsersTabs.class);
                startActivityForResult(intent, 0);

                return true;
            }
            case R.id.main_menu_pages:
            {
                // Launch the PagesTabs activity
                intent = new Intent().setClass(this, PagesTabs.class);
                startActivityForResult(intent, 0);

                return true;
            }
        }
        return false;
    }
}
