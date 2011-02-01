/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.AllFreshCmsApp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 *
 * @author Tymen
 */
public class P_Pages extends ListActivity
{
    private Context context = this;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        // Create an array of Strings, that will be put to our ListActivity
        String[] listArray = new String[]{"Home", "Organisation", "Employees", "About", "Contact", "Route", "Login"};

        // Create an ArrayAdapter, that will actually make the Strings above appear in the ListView
        this.setListAdapter(new ArrayAdapter<String>(this,
                         android.R.layout.simple_list_item_1, listArray));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        // Get the item that was clicked
        Object o = this.getListAdapter().getItem(position);

        Toast toast = Toast.makeText(context, o.toString(), Toast.LENGTH_SHORT);
        toast.show();
    }
}