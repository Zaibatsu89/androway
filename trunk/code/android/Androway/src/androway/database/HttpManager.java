package androway.database;

import android.content.Context;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.connection.ConnectionFactory;
import androway.connection.ConnectionManager;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.4
 */
public class HttpManager implements DatabaseManager
{
	
	ConnectionManager _httpManager;
	String _dbName;
	String _tempWebserviceUrl;
	
	public HttpManager(Context context) throws MaxPoolSizeReachedException
	{
		_httpManager = ConnectionFactory.acquireConnectionManager("http");
		_dbName = "logs";
		_tempWebserviceUrl = "http://www.androway.nl/dev/webservice.php";
	}

	public void executeNonQuery(String query) throws NotSupportedQueryTypeException
	{
		ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();

		data.add(new BasicNameValuePair("function", "executeNonQuery"));
		data.add(new BasicNameValuePair("dbName", _dbName));
		data.add(new BasicNameValuePair("query", query));

		_httpManager.post(_tempWebserviceUrl, data);
	}

	public ArrayList<ArrayList<String>> getData(String query)
	{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("function", "getData"));
		params.add(new BasicNameValuePair("dbName", _dbName));
		params.add(new BasicNameValuePair("query", query));

		return _httpManager.get(_tempWebserviceUrl, params);
	}
}