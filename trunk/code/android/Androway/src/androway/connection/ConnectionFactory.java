package androway.connection;

import android.app.Activity;
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
	private static Activity _mainActivity;
	private static ConnectionFactory _connectionFactory;
	private static Map _connectionManagersCollection;
	private static int _managerCount;
	private static int _maxPoolSize;

	private ConnectionFactory(Activity mainActivity)
	{
		_mainActivity = mainActivity;
		_connectionManagersCollection = new HashMap();
	}

	public ConnectionFactory getInstance()
	{
		if (_connectionFactory == null)
			_connectionFactory = new ConnectionFactory(_mainActivity);

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
					cm = new HttpManager(_mainActivity);
				else if (managerName.equals("bluetooth"))
					cm = new BluetoothManager(_mainActivity);

				_managerCount++;
			}

			return cm;
		}
		else
			throw new MaxPoolSizeReachedException(_mainActivity.getString(R.string.MaxPoolSizeReachedException));
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