package proj.androway.connection;

import proj.androway.connection.http.HttpManager;
import proj.androway.connection.bluetooth.BluetoothManager;
import android.content.Context;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.R;
import java.util.HashMap;
import java.util.Map;

/**
 * The ConnectionFactory class is used for retreiving and managing the instances for different connections.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public final class ConnectionFactory
{
    private static Map _connectionManagersCollection = new HashMap();
    private static int _managerCount = 0;
    private static int _maxPoolSize = 2;

    /**
     * Returns the desired IConnectionManager based on the given parameters
     * @param context       The application context
     * @param managerName   The name of the desired IConnectionManager
     * @return The desired IConnectionManager
     * @throws proj.androway.common.Exceptions.MaxPoolSizeReachedException Thrown when the 'object pool' exceeds the maximum pool size
     */
    public static IConnectionManager acquireConnectionManager(Context context, String managerName) throws MaxPoolSizeReachedException
    {
        IConnectionManager connectionManager = null;

        // Check if the manager allready exists in the object pool
        if (_connectionManagersCollection.containsKey(managerName))
            connectionManager = (IConnectionManager) _connectionManagersCollection.get(managerName);
        else
        {
            // The manager does not exist in the object pool yet, so create it
            if (managerName.equals(ConnectionManagerBase.TYPE_BLUETOOTH))
                connectionManager = new BluetoothManager( context);
            else if (managerName.equals(ConnectionManagerBase.TYPE_HTTP))
                connectionManager = new HttpManager(context);

            // Check if by adding this manager, the object pool size will be exceeded or not
            if (_managerCount + 1 <= _maxPoolSize)
            {
                // Store the created manager in the object pool
                _connectionManagersCollection.put(managerName, connectionManager);
                _managerCount++;
            }
            else
                throw new MaxPoolSizeReachedException(context.getString(R.string.MaxPoolSizeReachedException));
        }

        return connectionManager;
    }

    /**
     * Remove the IConnectionManager with the given manager name
     * @param managerName   The name (key) of the IConnectionManager
     */
    public static void releaseConnectionManager(String managerName)
    {
        if (_connectionManagersCollection.containsKey(managerName))
        {
            _connectionManagersCollection.remove(managerName);
            _managerCount--;
        }
    }

    /**
     * Set the maximum size of the object pool
     * @param maxPoolSize   The maximum size of the object pool
     */
    public static void setMaxPoolSize(int maxPoolSize)
    {
        _maxPoolSize = maxPoolSize;
    }
}