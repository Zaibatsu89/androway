package androway.database;

import android.content.Context;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.connection.ConnectionFactory;
import androway.connection.IConnectionManager;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.4
 */
public class HttpManager implements IDatabaseManager
{	
	private IConnectionManager _httpManager;
	private String _tempWebserviceUrl;
	
	public HttpManager(Context context) throws MaxPoolSizeReachedException
	{
		_httpManager = ConnectionFactory.acquireConnectionManager(context, "http");
		_tempWebserviceUrl = "http://m.androway.nl/dev/webservice.php";
	}

	public boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryTypeException
	{
		ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();

		data.add(new BasicNameValuePair("function", "executeNonQuery"));
		data.add(new BasicNameValuePair("dbName", dbName));
		data.add(new BasicNameValuePair("query", query));

		return _httpManager.post(_tempWebserviceUrl, data);
	}

	public Map<String, Object> getData(String dbName, String query)
	{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("function", "getData"));
		params.add(new BasicNameValuePair("dbName", dbName));
		params.add(new BasicNameValuePair("query", query));

		return _httpManager.get(_tempWebserviceUrl, params);
	}
}