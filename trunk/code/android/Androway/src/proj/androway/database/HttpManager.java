package proj.androway.database;

import android.content.Context;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.connection.ConnectionFactory;
import proj.androway.connection.ConnectionManagerBase;
import proj.androway.connection.IConnectionManager;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.41
 */
public class HttpManager extends DatabaseManagerBase
{	
    private IConnectionManager _httpManager;

    public HttpManager(Context context) throws MaxPoolSizeReachedException
    {
        _httpManager = ConnectionFactory.acquireConnectionManager(context, ConnectionManagerBase.TYPE_HTTP);
    }

    public boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryTypeException
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();

        data.add(new BasicNameValuePair("function", "executeNonQuery"));
        data.add(new BasicNameValuePair("dbName", dbName));
        data.add(new BasicNameValuePair("query", query));

        return _httpManager.post(Constants.WEBSERVICE_URL, data);
    }

    public Map<String, Object> getData(String dbName, String query)
    {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "getData"));
        params.add(new BasicNameValuePair("dbName", dbName));
        params.add(new BasicNameValuePair("query", query));

        return _httpManager.get(Constants.WEBSERVICE_URL, params);
    }
}