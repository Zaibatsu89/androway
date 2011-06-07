package proj.androway.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.NameValuePair;

/**
 * IConnectionManager is the interface used for all types of connections (f.e. Bluetooth, Http, ...)
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public interface IConnectionManager
{
    /**
     * Open (start) the connection using the given address
     * @param address   The address
     * @return Whether the connection was succesfully established or not
     */
    public abstract boolean open(String address);

    /**
     * Open (start) the connection using the given address and the given login credentials
     * @param address   The address
     * @param data      The login credentials (keys should be 'email' and 'password')
     * @return Whether the connection was succesfully established or not
     */
    public abstract boolean open(String address, ArrayList<NameValuePair> data);

    /**
     * Close the current bluetooth connection
     */
    public abstract void close();

    /**
     * Close the current bluetooth connection Before closing the connection,
     * send a logout post.
     * @param address   The address
     */
    public abstract void close(String address);

    /**
     * Check the connection
     * @return Whether there is a connection or not
     */
    public abstract boolean checkConnection();

    /**
     * Post (send) the given data using the given address
     * @param address   The address
     * @param data      The data to send
     * @return Whether the post was succesful or not
     */
    public abstract boolean post(String address, ArrayList<NameValuePair> data);

    /**
     * Send a get request using the given address
     * @param address   The address
     * @param params    The parameters to send
     * @return The returned data
     * @throws IOException Thrown if an error occured while executing the get request
     */
    public abstract Map<String, Object> get(String address, ArrayList<NameValuePair> params) throws IOException;
}