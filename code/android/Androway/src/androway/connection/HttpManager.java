package androway.connection;

import androway.common.Exceptions.HttpGetRequestFailedException;
import androway.common.Exceptions.HttpPostRequestFailedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
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
 * @since 10-03-2011
 * @version 0.41
 */
public class HttpManager implements ConnectionManager
{
	private DefaultHttpClient httpClient;
    private ResponseHandler<String> responseHandler;
    private List<NameValuePair> postData;

	public HttpManager()
	{
		httpClient = new DefaultHttpClient();
        responseHandler = new BasicResponseHandler();
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
		HttpPost httpPost = new HttpPost(address);

        try
        {
            // Assign post data
            httpPost.setEntity(new UrlEncodedFormEntity(data));
			success = true;
        }
        catch(IOException ex)
        {
			try {
				throw new HttpPostRequestFailedException("Assignment of post data failed.");
			} catch (HttpPostRequestFailedException ex1) {
				Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex1);
			}
        }

        // Return the result
		return success;
	}

	public ArrayList get(String address, ArrayList<NameValuePair> params)
	{	
		ArrayList result = new ArrayList();

        try
        {
			// Add the given paramaters from the ArrayList to the address (url)
			int i = 0;
			for(NameValuePair nameValuePair : params)
			{
				char splitter = '&';
				if(i == 0)
					splitter = '?';

				address += splitter + nameValuePair.getName() + "=" + nameValuePair.getValue().toString();

				i++;
			}

			// Create a new HttpGet instance and assign the address (uri)
			HttpGet httpGet = new HttpGet(address);
			// Fetch the response and assign it to a JSONArray
			JSONArray jsonItems = new JSONArray(httpClient.execute(httpGet, responseHandler));
		} catch (JSONException ex) {
			Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
			try {
				throw new HttpGetRequestFailedException("Could not fetch the response.");
			} catch (HttpGetRequestFailedException ex1) {
				Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex1);
			}
        }

		return result;
	}

	public ArrayList deserializeJson(ArrayList resultHolder, JSONArray jsonArray) throws JSONException
	{
		int length = jsonArray.length();

		for(int i = length - 1; i > -1; i--)
		{
			// Retrieve the data of the JSON item and parse it
			JSONObject jsonChild = (JSONObject)jsonArray.get(i);
		}

		return resultHolder;
	}
}