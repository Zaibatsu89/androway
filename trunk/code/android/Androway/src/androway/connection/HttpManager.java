package androway.connection;

import android.content.Context;
import androway.common.Exceptions.HttpGetRequestFailedException;
import androway.common.Exceptions.HttpPostRequestFailedException;
import androway.ui.R;
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
public class HttpManager extends ConnectionManagerBase
{
	private Context _context;

	public HttpManager(Context context)
	{
		_context = context;
	}

	// Not needed for the http manager
	public boolean open(String address)
	{
        return true;
	}

	// Not needed for the http manager
	public void close() { }

	public boolean post(String address, ArrayList<NameValuePair> data)
	{
		boolean success = false;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(address);

        try
        {
            // Assign post data
            httpPost.setEntity(new UrlEncodedFormEntity(data));

			// Execute the HttpPost request with the client and pass the response handler for the result
			httpClient.execute(httpPost);

			success = true;
        }
        catch(IOException ex)
        {
			try {
				throw new HttpPostRequestFailedException(_context.getString(R.string.HttpPostRequestFailedException));
			} catch (HttpPostRequestFailedException ex1) {
				Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex1);
			}
        }

        // Return the result
		return success;
	}

	public Map<String, Object> get(String address, ArrayList<NameValuePair> params)
	{
		Map result = null;

		HttpClient httpClient = new DefaultHttpClient();
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
			String response = httpClient.execute(httpGet, responseHandler);

			// Assign the response to a JSONArray
			JSONArray jsonItems = new JSONArray(response);

			result = deserializeJson(new HashMap<String, Object>(), jsonItems);
		} catch (JSONException ex)
		{
			Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex)
		{
			try {
				throw new HttpGetRequestFailedException(_context.getString(R.string.HttpGetRequestFailedException));
			} catch (HttpGetRequestFailedException ex1) {
				Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex1);
			}
        }

		return result;
	}

	public Map<String, Object> deserializeJson(Map<String, Object> resultHolder, JSONArray jsonArray) throws JSONException
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