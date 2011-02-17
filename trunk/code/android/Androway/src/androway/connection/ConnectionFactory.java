package androway.connection;

import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.ui.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Class ConnectionFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public final class ConnectionFactory
{
	private static ConnectionFactory _connectionFactory;
	private static Map _connectionManagersCollection;
	private static int _managerCount;
	private static int _maxPoolSize;

	private ConnectionFactory()
	{
		_connectionManagersCollection = new HashMap();
	}

	public ConnectionFactory getInstance()
	{
		if (_connectionFactory == null)
			_connectionFactory = new ConnectionFactory();

		return _connectionFactory;
	}

	public static ConnectionManager acquireConnectionManager(String managerName) throws MaxPoolSizeReachedException
	{
		if (_managerCount < _maxPoolSize)
		{
			ConnectionManager cm = null;

			if (_connectionManagersCollection.containsKey(managerName))
				cm = (ConnectionManager) _connectionManagersCollection.get(managerName);
			else
			{
				if (managerName.equals("http"))
					cm = new HttpManager();
				else if (managerName.equals("bluetooth"))
					cm = new BluetoothManager();

				_managerCount++;
			}

			return cm;
		}
		else
			throw new MaxPoolSizeReachedException(String.valueOf(R.string.MaxPoolSizeReachedException));
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