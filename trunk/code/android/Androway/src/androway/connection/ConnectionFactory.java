package androway.connection;

import android.content.Context;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.ui.R;
import java.util.HashMap;
import java.util.Map;

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
	private static int _managerCount;
	private static int _maxPoolSize = 2;

	private ConnectionFactory() {}

	public ConnectionFactory getInstance()
	{
		if (_connectionFactory == null)
			_connectionFactory = new ConnectionFactory();

		return _connectionFactory;
	}

	public static IConnectionManager acquireConnectionManager(Context context, String managerName)
		throws MaxPoolSizeReachedException
	{
		if (_managerCount < _maxPoolSize)
		{
			IConnectionManager cm = null;

			if (_connectionManagersCollection.containsKey(managerName))
				cm = (IConnectionManager) _connectionManagersCollection.get(managerName);
			else
			{
				if (managerName.equals(ConnectionManagerBase.TYPE_BLUETOOTH))
					cm = new BluetoothManager();
				else if (managerName.equals(ConnectionManagerBase.TYPE_HTTP))
					cm = new HttpManager(context);

				_managerCount++;
			}

			return cm;
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