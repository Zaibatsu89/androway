package proj.androway.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import proj.androway.common.Exceptions.HttpGetRequestFailedException;
import proj.androway.common.Exceptions.HttpPostRequestFailedException;
import proj.androway.R;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class HttpManager sets up the http
 * connection between the Android phone
 * and the androway.nl domain.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.42
 */
public class HttpManager extends ConnectionManagerBase implements Runnable
{
    private Context _context;
    private HttpClient _httpClient;
    private boolean _running = true;
    Thread _myThread;

    public HttpManager(Context context)
    {
        _context = context;
        _httpClient = new DefaultHttpClient();
    }

    public synchronized void run()
    {
        //while(_running) { }
    }

    // Opening the connection, to login to the remote site
    public synchronized boolean open(String address, ArrayList<NameValuePair> data)
    {
        _myThread = new Thread(this);
        _myThread.start();

        boolean emailAvailable = false;
        boolean passwordAvailable = false;
        boolean result = false;

        // Check if the email and password are set
        for(NameValuePair nameValue : data)
        {
            if(nameValue.getName().equals("email"))
                    emailAvailable = true;
            else if(nameValue.getName().equals("password"))
                    passwordAvailable = true;
        }

        // If the email and password are set and there is an http connection,
        // send a login http request
        if(emailAvailable && passwordAvailable && checkConnection())
        {
            data.add(new BasicNameValuePair("authType", "login"));
            Map loginResult = this.get(address, data);

            // Return whether the login succeeded or not
            result = Boolean.parseBoolean((String)loginResult.get("success"));
        }

        return result;
    }

    // When closing the connection, logout from the remote site
    public synchronized void close(String address)
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("authType", "logout"));

        // Send a logout post request
        this.post(address, data);

        _running = false;
    }

    // Check the current connection
    public synchronized boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // Test if there is an http connection (mobile or wi-fi)
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public synchronized boolean post(String address, ArrayList<NameValuePair> data)
    {
        boolean success = false;
        HttpPost httpPost = new HttpPost(address);

        try
        {
            // Assign post data
            httpPost.setEntity(new UrlEncodedFormEntity(data));

            // Execute the HttpPost request with the client and pass the response handler for the result
            _httpClient.execute(httpPost);

            success = true;
        }
        catch(IOException ex)
        {
            try
            {
                throw new HttpPostRequestFailedException(_context.getString(R.string.HttpPostRequestFailedException));
            }
            catch (HttpPostRequestFailedException ex1)
            {
                Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Return the result
        return success;
    }

    public synchronized Map<String, Object> get(String address, ArrayList<NameValuePair> params)
    {
        Map result = null;

        HttpGet httpGet;
        BasicResponseHandler responseHandler = new BasicResponseHandler();

        try
        {
            // Add the given parameters from the ArrayList to the address (url)
            int i = 0;
            for(NameValuePair nameValuePair : params)
            {
                char splitter = '&';
                if(i == 0)
                    splitter = '?';

                if(nameValuePair.getName().equalsIgnoreCase("query"))
                {
                    String value = URLEncoder.encode(nameValuePair.getValue().toString());
                    int index = value.toLowerCase().indexOf("from");

                    address += splitter + nameValuePair.getName() + "1=" + value.substring(0, index);
                    address += splitter + nameValuePair.getName() + "2=" + value.substring(index);
                }
                else
                    address += splitter + nameValuePair.getName() + "=" + URLEncoder.encode(nameValuePair.getValue().toString());

                i++;
            }

            // Assign the created url to the HttpGet object
            httpGet = new HttpGet(address);

            // Execute the Http request and store the response
            String response = _httpClient.execute(httpGet, responseHandler);

            // Deserialize the received json string and assign the result to result;
            result = deserializeJson(new HashMap<String, Object>(), response);
        }
        catch (JSONException ex)
        {
            Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            try
            {
                throw new HttpGetRequestFailedException(_context.getString(R.string.HttpGetRequestFailedException));
            }
            catch (HttpGetRequestFailedException ex1)
            {
                Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return result;
    }

    public synchronized Map<String, Object> deserializeJson(Map<String, Object> resultHolder, String jsonString) throws JSONException
    {
        if(!jsonString.isEmpty())
        {
            // Try to assign the response to a JSONArray,
            // if it fails it is not an array, so assign it to a JSONObject.
            JSONArray jsonItems = null;
            JSONObject jsonItem = null;
            try
            {
                // Try to parse the string to a JSONArray
                jsonItems = new JSONArray(jsonString);
            }
            catch (JSONException ex)
            {
                try
                {
                    // The parsing to JSONArray failed, handle as JSONObject in stead.
                    jsonItem = new JSONObject(jsonString);
                }
                catch (JSONException ex1)
                {
                    return resultHolder;
                }
            }

            // If jsonItems is null, it means the json string is not a JSONArray but
            // a single level JSONObject. Process the single level JSONObject,
            // else process as a JSONArray.
            if(jsonItems == null)
            {
                JSONArray children = jsonItem.names();
                int childrenLength = children.length();

                for(int j = 0; j < childrenLength; j++)
                {
                    String name = children.getString(j);
                    String value = jsonItem.getString(name);

                    if(value.indexOf("{") == 1 && value.lastIndexOf("}") == value.length() - 1)
                        resultHolder.put(name, deserializeJson(new HashMap<String, Object>(), jsonItem.getJSONArray(name)));
                    else
                        resultHolder.put(name, value);
                }
            }
            else
                resultHolder = deserializeJson(resultHolder, jsonItems);
        }

        return resultHolder;
    }

    public synchronized Map<String, Object> deserializeJson(Map<String, Object> resultHolder, JSONArray jsonArray) throws JSONException
    {
        int length = jsonArray.length();

        for(int i = length - 1; i > -1; i--)
        {
            Map<String, Object> elementHolder = new HashMap<String, Object>();

            // Retrieve the data of the JSON item and parse it
            JSONObject jsonElement = (JSONObject)jsonArray.get(i);

            JSONArray children = jsonElement.names();
            int childrenLength = children.length();

            for(int j = 0; j < childrenLength; j++)
            {
                String name = children.getString(j);
                String value = jsonElement.getString(name);

                if(value.indexOf("{") == 1 && value.lastIndexOf("}") == value.length() - 1)
                    elementHolder.put(name, deserializeJson(new HashMap<String, Object>(), jsonElement.getJSONArray(name)));
                else
                    elementHolder.put(name, value);
            }

            resultHolder.put("row" + i, elementHolder);
        }

        return resultHolder;
    }
}