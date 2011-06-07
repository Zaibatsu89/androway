package proj.androway.database.http;

import android.content.Context;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryException;
import proj.androway.connection.ConnectionFactory;
import proj.androway.connection.ConnectionManagerBase;
import proj.androway.connection.IConnectionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import proj.androway.common.Settings;
import proj.androway.database.DatabaseManagerBase;

/**
 * The HttpManager class is the database manager for a remote database through an http connection
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class HttpManager extends DatabaseManagerBase
{
    private IConnectionManager _httpManager;

    /**
     * The constructor for the HttpManager
     * @param context   The application context
     */
    public HttpManager(Context context)
    {
        super(context);
        
        try
        {
            _httpManager = ConnectionFactory.acquireConnectionManager(context, ConnectionManagerBase.TYPE_HTTP);
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(HttpManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Open (initialize) the http database manager (signs into into the remote server)
     * @return Whether the opening (initialization) was succesful or not
     */
    public boolean open()
    {
        // Get the login credentials from the settings
        ArrayList<NameValuePair> loginData = new ArrayList<NameValuePair>();
        loginData.add(new BasicNameValuePair("email", Settings.USER_EMAIL));
        loginData.add(new BasicNameValuePair("password", Settings.USER_PASSWORD));

        // Open the http connection (login) with the login url and login credentials.
        return _httpManager.open(Constants.AUTH_WEBSERVICE_URL, loginData);
    }

    /**
     * Close the database connection (close the http connection)
     */
    public void close()
    {
        _httpManager.close(Constants.AUTH_WEBSERVICE_URL);
    }

    /**
     * Execute the given SQL query (can be used for INSERT, UPDATE or DELETE commands)
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return Whether the query was succesfully executed or not
     * @throws proj.androway.common.Exceptions.NotSupportedQueryException
     */
    public boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryException
    {
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();

        data.add(new BasicNameValuePair("function", "executeNonQuery"));
        data.add(new BasicNameValuePair("dbName", dbName));
        data.add(new BasicNameValuePair("query", query));

        return _httpManager.post(Constants.WEBSERVICE_URL, data);
    }

    /**
     * Get data based on the given SQL query
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return The data result of the query
     */
    public Map<String, Object> getData(String dbName, String query)
    {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "getData"));
        params.add(new BasicNameValuePair("dbName", dbName));
        params.add(new BasicNameValuePair("query", query));
        
        try
        {
            // If the data was succesfully retreived, return the returned map.
            // If it fails, the exception will be cought and will be handled in the
            // catch block.
            return _httpManager.get(Constants.WEBSERVICE_URL, params);
        }
        catch (IOException ex)
        {
            // If the retreiving of the data failed, return an empty (new) HashMap
            return new HashMap();
        }
    }
}