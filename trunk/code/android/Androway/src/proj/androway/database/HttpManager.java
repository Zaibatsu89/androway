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
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.41
 */
public class HttpManager extends DatabaseManagerBase
{
    private IConnectionManager _httpManager;
    private Thread _connectionThread;

    public HttpManager(SharedObjects sharedObjects, Context context) throws MaxPoolSizeReachedException
    {
        super(sharedObjects, context);
        _httpManager = ConnectionFactory.acquireConnectionManager(sharedObjects, context, ConnectionManagerBase.TYPE_HTTP);
    }

    /*
     * Initialize the http database manager by logging in to the remote server first.
     */
    public boolean init()
    {
        //_connectionThread = new Thread(_httpManager);
        //_connectionThread.start();

        // Get the login credentials from the settings
        ArrayList<NameValuePair> loginData = new ArrayList<NameValuePair>();
        loginData.add(new BasicNameValuePair("email", Settings.USER_EMAIL));
        loginData.add(new BasicNameValuePair("password", Settings.USER_PASSWORD));

        // Open the http connection (login) with the login url and login credentials.
        return _httpManager.open(Constants.AUTH_WEBSERVICE_URL, loginData);
    }

    public void close()
    {
        _httpManager.close(Constants.AUTH_WEBSERVICE_URL);
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