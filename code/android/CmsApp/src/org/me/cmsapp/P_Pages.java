/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.cmsapp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tymen
 */
public class P_Pages extends ListActivity
{
    private Context context = this;
    private HttpHandler httpHandler;
    private JSONObject json;
    private String url = "http://t-squad.nl/http.php";
    private ArrayList listArray = new ArrayList();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        // Assemble data to post
        ArrayList data = new ArrayList();
        data.add(new ArrayList());
            ((ArrayList)data.get(data.size()-1)).add("naam");
            ((ArrayList)data.get(data.size()-1)).add("Tymen");
        data.add(new ArrayList());
            ((ArrayList)data.get(data.size()-1)).add("leeftijd");
            ((ArrayList)data.get(data.size()-1)).add("20");

        try
        {
            // postData returns the post result. Create a JSON object out of the result.
            json = new JSONObject(postData(url, data));

            // Parse the JSON object into an array of names
            JSONArray jsonItems = json.names();

            for(int i = jsonItems.length()-1; i > -1; i--)
            {
                // Retrieve the data of the JSON item and parse it into an array of names and an array of values
                JSONObject jsonChild = (JSONObject)json.get(jsonItems.getString(i));

                listArray.add(jsonChild.getString("page_name"));
            }
        }
        catch (JSONException ex)
        {
            Logger.getLogger(P_Pages.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create an ArrayAdapter, that will actually make the Strings above appear in the ListView
        this.setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listArray));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        // Get the item that was clicked
        Object o = this.getListAdapter().getItem(position);

        // Parse the JSON object into an array of names
        /*JSONArray jsonItems = json.names();

        // Loop the json array, and check until we find the clicked item (search by name)
        for(int i = jsonItems.length()-1; i > -1; i--)
        {
            try
            {
                // Retrieve the data of the JSON item and parse it into an array of names and an array of values
                JSONObject jsonChild = (JSONObject) json.get(jsonItems.getString(i));
            }
            catch (JSONException ex)
            {
                Logger.getLogger(P_Pages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        */

        Toast.makeText(context, "Loading page: \""+o.toString()+"\"", Toast.LENGTH_SHORT).show();
    }

    /*  Posts data, and returns the result
        ArrayList data is a multidimensional ArrayList
    */
    private String postData(String url, ArrayList data)
    {
        // Create a http handler and establish a connection
        httpHandler = new HttpHandler();
        httpHandler.establishConnection(url);

        // Add the given data, to the HttpHandler as post data
        for(int i = 0; i < data.size(); i++)
        {
            ArrayList item = (ArrayList)data.get(i);
            httpHandler.addPostData((String)item.get(0), (String)item.get(1));
        }

        // Fetch and return the result
        return httpHandler.fetchResult();
    }
}