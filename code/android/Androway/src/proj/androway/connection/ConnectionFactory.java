package proj.androway.connection;

import android.content.Context;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.R;
import java.util.HashMap;
import java.util.Map;
import proj.androway.common.SharedObjects;

/**
 * Class ConnectionFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass IConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.32
 */
public final class ConnectionFactory
{
    private static ConnectionFactory _connectionFactory;
    private static Map _connectionManagersCollection = new HashMap();
    private static int _managerCount = 0;
    private static int _maxPoolSize = 2;

    private ConnectionFactory() {}

    public ConnectionFactory getInstance()
    {
        if (_connectionFactory == null)
            _connectionFactory = new ConnectionFactory();

        return _connectionFactory;
    }

    public static IConnectionManager acquireConnectionManager(SharedObjects sharedObjects, Context context, String managerName) throws MaxPoolSizeReachedException
    {
        if (_managerCount <= _maxPoolSize)
        {
            IConnectionManager connectionManager = null;

            if (_connectionManagersCollection.containsKey(managerName))
                connectionManager = (IConnectionManager) _connectionManagersCollection.get(managerName);
            else
            {
                if (managerName.equals(ConnectionManagerBase.TYPE_BLUETOOTH))
                    connectionManager = new BluetoothManager(sharedObjects, context);
                else if (managerName.equals(ConnectionManagerBase.TYPE_HTTP))
                    connectionManager = new HttpManager(sharedObjects, context);

                _connectionManagersCollection.put(managerName, connectionManager);
                _managerCount++;
            }

            return connectionManager;
        }
        else
            throw new MaxPoolSizeReachedException(context.getString(R.string.MaxPoolSizeReachedException));
    }

    public static void releaseConnectionManager(String managerName)
    {
        if (_connectionManagersCollection.containsKey(managerName))
        {
            _connectionManagersCollection.remove(managerName);
            _managerCount--;
        }
    }

    public static void setMaxPoolSize(int maxPoolSize)
    {
        _maxPoolSize = maxPoolSize;
    }
}