package androway.connection;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

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
    private HttpPost httpPost;
    private List<NameValuePair> postData;

	public HttpManager() {}

	public boolean open(String address) {
		// Create connection
        httpClient = new DefaultHttpClient();
        responseHandler = new BasicResponseHandler();
        httpPost = new HttpPost(address);
        postData = new ArrayList<NameValuePair>(2);
        return true;
	}

	public void close() {
		httpPost.abort();
	}

	public void post(String function, String dbName, String query) {
//		postData.add(new BasicNameValuePair(
//			data.get(0).toString(), data.get(1).toString()));

	}

	public ArrayList get(String function, String dbName, String query) {
//		ArrayList result = new ArrayList();
//		String strResult = "";
//        try
//        {
//            // Assign post data
//            httpPost.setEntity(new UrlEncodedFormEntity(postData));
//            // Fetch the response
//            strResult = httpClient.execute(httpPost, responseHandler);
//        }
//        catch(IOException ex)
//        {
//            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        // Return the result
//		result.add(strResult);
//        return result;
		return new ArrayList();
	}
}