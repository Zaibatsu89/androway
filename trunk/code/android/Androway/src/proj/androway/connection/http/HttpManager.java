package proj.androway.connection.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
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
import proj.androway.connection.ConnectionManagerBase;

/**
 * The HttpManager class sets up the http connection
 * between the Android device and the remote server.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class HttpManager extends ConnectionManagerBase
{
    public static int sessionId;
    public static int userId;
    
    private HttpClient _httpClient;

    /**
     * The constructor for the HttpManager
     * @param context   The application context
     */
    public HttpManager(Context context)
    {
        super(context);
        _httpClient = new DefaultHttpClient();
    }

    /**
     * Open (start) the connection to the remote server using the given address.
     * This will also login to the remote server, using the given credentials.
     * @param address   The web address (url)
     * @param data      The login credentials (keys should be 'email' and 'password')
     * @return Whether the connection was succesfully established or not
     */
    public boolean open(String address, ArrayList<NameValuePair> data)
    {
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
            try
            {
                data.add(new BasicNameValuePair("authType", "login"));
                data.add(new BasicNameValuePair("fromApp", "true"));
                Map loginResult = get(address, data);

                // Store the result of 'success' (true || false) in the result
                result = Boolean.parseBoolean((String) loginResult.get("success"));

                // If the login was succesfull, the session id and the user id are also returned.
                if (result)
                {
                    // Store the received session id and user id for this connection
                    sessionId = Integer.parseInt((String) loginResult.get("sessionId"));
                    userId = Integer.parseInt((String) loginResult.get("userId"));
                }
            }
            catch (IOException ex)
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * This function is not supported for the HttpManager.
     * Use the parameterized version instead.
     */
    public void close()
    {
        throw new UnsupportedOperationException("Not supported for the HttpManager.");
    }

    /**
     * Close the active Http connection. Before closing the connection,
     * send a logout post request.
     * @param address   The web address (url)
     */
    public void close(String address)
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("authType", "logout"));

        // Send a logout post request if connected
        if(checkConnection())
            post(address, data);
    }

    /**
     * Check the current Http connection
     * @return Whether there is an internet connection or not
     */
    public synchronized boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // Test if there is an http connection (mobile or wi-fi)
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    /**
     * Post (send) the given data to the remote server with the given address
     * @param address   The web address (url)
     * @param data      The data to send
     * @return Whether the post was succesful or not
     */
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


    /**
     * Send a get request to the remote server with the given address
     * @param address   The web address (url)
     * @param params    The parameters to send
     * @return The returned data
     * @throws IOException Thrown if an error occured while executing the get request
     */
    public synchronized Map<String, Object> get(String address, ArrayList<NameValuePair> params) throws IOException
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

        return result;
    }

    /**
     * Deserialize the given json string to a map
     * @param resultHolder  The map to contain the deserialized data
     * @param jsonString    The json data string
     * @return The deserialized json in the resultHolder map
     * @throws JSONException Thrown if a json format error occurs
     */
    public synchronized Map<String, Object> deserializeJson(Map<String, Object> resultHolder, String jsonString) throws JSONException
    {
        if(!TextUtils.isEmpty(jsonString))
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

    /**
     * Deserialize the given json string to a map
     * @param resultHolder  The map to contain the deserialized data
     * @param jsonArray     The json array
     * @return The deserialized json in the resultHolder map
     * @throws JSONException Thrown if a json format error occurs
     */
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