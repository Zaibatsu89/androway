/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.cmsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Tymen
 */
public class HttpHandler
{
    private DefaultHttpClient httpClient;
    private ResponseHandler<String> responseHandler;
    private HttpPost httpPost;
    private List<NameValuePair> postData;


    /* Create a http connection */
    public boolean establishConnection(String url)
    {
        // Create connection
        httpClient = new DefaultHttpClient();
        responseHandler = new BasicResponseHandler();
        httpPost = new HttpPost(url);
        postData = new ArrayList<NameValuePair>(2);
        return true;
    }

    /* Add data to the postData array */
    public void addPostData(String name, String value)
    {
        postData.add(new BasicNameValuePair(name, value));
    }

    /* Perform the action, and fetch the result */
    public String fetchResult()
    {
        String result = "";
        try
        {
            // Assign post data
            httpPost.setEntity(new UrlEncodedFormEntity(postData));
            // Fetch the response
            result = httpClient.execute(httpPost, responseHandler);
        }
        catch(IOException ex)
        {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return the result
        return result;
    }
}